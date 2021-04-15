package com.ivianuu.essentials.backup

import android.content.ContentResolver
import android.content.Intent
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.data.DataDir
import com.ivianuu.essentials.processrestart.ProcessRestarter
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.toIntentKey
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

typealias CreateBackupUseCase = suspend () -> Result<Unit, Throwable>

@Given
fun createBackupUseCase(
    @Given backupDir: BackupDir,
    @Given backupFiles: Set<BackupFile>,
    @Given buildInfo: BuildInfo,
    @Given dataDir: DataDir,
    @Given dispatcher: IODispatcher,
    @Given logger: Logger,
    @Given navigator: Navigator,
    @Given scope: ScopeCoroutineScope<AppGivenScope>
): CreateBackupUseCase = {
    runCatching {
        withContext(scope.coroutineContext + dispatcher) {
            val dateFormat = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss")
            val backupFileName =
                "${buildInfo.packageName.replace(".", "_")}_${dateFormat.format(Date())}"

            val backupFile = backupDir.resolve("$backupFileName.zip")
                .also {
                    it.parentFile.mkdirs()
                    it.createNewFile()
                }

            val zipOutputStream = ZipOutputStream(backupFile.outputStream())

            backupFiles
                .flatMap { it.walkTopDown() }
                .filterNot { it.isDirectory }
                .filterNot { it.absolutePath in BACKUP_BLACKLIST }
                .filter { it.exists() }
                .forEach { file ->
                    logger.d { "backup file $file" }
                    val entry = ZipEntry(file.relativeTo(dataDir).toString())
                    zipOutputStream.putNextEntry(entry)
                    file.inputStream().copyTo(zipOutputStream)
                    zipOutputStream.closeEntry()
                }

            zipOutputStream.close()

            navigator.push(ShareBackupFileKey(backupFile.absolutePath))
        }
    }
}

typealias RestoreBackupUseCase = suspend () -> Result<Unit, Throwable>

@Given
fun restoreBackupUseCase(
    @Given contentResolver: ContentResolver,
    @Given dataDir: DataDir,
    @Given dispatcher: IODispatcher,
    @Given logger: Logger,
    @Given navigator: Navigator,
    @Given processRestarter: ProcessRestarter,
    @Given scope: ScopeCoroutineScope<AppGivenScope>
): RestoreBackupUseCase = {
    runCatching {
        withContext(scope.coroutineContext + dispatcher) {
            val uri = navigator.pushForResult(
                Intent.createChooser(
                    Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "application/zip"
                    },
                    ""
                ).toIntentKey()
            )?.data?.data ?: return@withContext

            val zipInputStream = ZipInputStream(contentResolver.openInputStream(uri)!!)

            generateSequence { zipInputStream.nextEntry }
                .forEach { entry ->
                    val file = dataDir.resolve(entry.name)
                    logger.d { "restore file $file" }
                    if (!file.exists()) {
                        file.parentFile.mkdirs()
                        file.createNewFile()
                    }
                    zipInputStream.copyTo(file.outputStream())
                    zipInputStream.closeEntry()
                }

            zipInputStream.close()

            processRestarter()
        }
    }
}

private val BACKUP_BLACKLIST = listOf(
    "com.google.android.datatransport.events",
    "com.google.android.datatransport.events-journal"
)

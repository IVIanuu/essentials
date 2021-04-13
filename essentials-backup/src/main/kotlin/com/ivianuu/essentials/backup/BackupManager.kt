package com.ivianuu.essentials.backup

import android.content.ContentResolver
import android.content.Intent
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.data.DataDir
import com.ivianuu.essentials.processrestart.ProcessRestarter
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.ActivityResultLauncher
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

@Given
class BackupManager(
    @Given private val activityResultLauncher: ActivityResultLauncher,
    @Given private val backupDir: BackupDir,
    @Given private val backupFiles: Set<BackupFile>,
    @Given private val buildInfo: BuildInfo,
    @Given private val contentResolver: ContentResolver,
    @Given private val dataDir: DataDir,
    @Given private val ioDispatcher: IODispatcher,
    @Given private val logger: Logger,
    @Given private val navigator: Navigator,
    @Given private val processRestarter: ProcessRestarter,
    @Given private val scope: ScopeCoroutineScope<AppGivenScope>
) {
    suspend fun restoreBackup() = runCatching {
        withContext(scope.coroutineContext + ioDispatcher) {
            val uri = activityResultLauncher.startActivityForResult(
                Intent.createChooser(
                    Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "application/zip"
                    },
                    ""
                )
            ).data?.data ?: return@withContext

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

    suspend fun createBackup() = runCatching {
        withContext(scope.coroutineContext + ioDispatcher) {
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

    private companion object {
        private val BACKUP_BLACKLIST = listOf(
            "com.google.android.datatransport.events",
            "com.google.android.datatransport.events-journal"
        )
    }
}

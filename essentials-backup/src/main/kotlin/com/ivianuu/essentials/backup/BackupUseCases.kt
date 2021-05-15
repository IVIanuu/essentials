package com.ivianuu.essentials.backup

import android.content.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.processrestart.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import java.text.*
import java.util.*
import java.util.zip.*

typealias CreateBackupUseCase = suspend () -> Result<Unit, Throwable>

@Given fun createBackupUseCase(
  @Given backupDir: BackupDir,
  @Given backupFiles: Set<BackupFile>,
  @Given buildInfo: BuildInfo,
  @Given dataDir: DataDir,
  @Given dispatcher: IODispatcher,
  @Given logger: Logger,
  @Given navigator: Navigator,
  @Given scope: GivenCoroutineScope<AppGivenScope>
): CreateBackupUseCase = {
  catch {
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

@Given fun restoreBackupUseCase(
  @Given contentResolver: ContentResolver,
  @Given dataDir: DataDir,
  @Given dispatcher: IODispatcher,
  @Given logger: Logger,
  @Given navigator: Navigator,
  @Given processRestarter: ProcessRestarter,
  @Given scope: GivenCoroutineScope<AppGivenScope>
): RestoreBackupUseCase = {
  catch {
    withContext(scope.coroutineContext + dispatcher) {
      val uri = navigator.push(
        Intent.createChooser(
          Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/zip"
          },
          ""
        ).toIntentKey()
      )?.getOrNull()?.data?.data ?: return@withContext

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

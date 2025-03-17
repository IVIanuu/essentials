/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.backup

import android.app.*
import android.content.*
import android.content.pm.*
import android.icu.text.*
import androidx.compose.ui.util.*
import androidx.core.content.*
import com.github.michaelbull.result.*
import essentials.*
import essentials.app.*
import essentials.coroutines.*
import essentials.data.*
import essentials.logging.*
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import java.io.*
import java.util.*
import java.util.zip.*

@Tag typealias createBackupResult = Unit
typealias createBackup = suspend () -> createBackupResult

@Provide suspend fun createBackup(
  backupDestinationDir: BackupDestinationDir,
  backupFiles: List<BackupFile>,
  appConfig: AppConfig,
  context: Context,
  coroutineContexts: CoroutineContexts,
  dirs: AppDirs,
  logger: Logger = inject,
  navigator: Navigator
): createBackupResult = withContext(coroutineContexts.io) {
  val dateFormat = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss")
  val backupFileName =
    "${appConfig.packageName.replace(".", "_")}_${dateFormat.format(Date())}"

  val backupFile = backupDestinationDir.resolve("$backupFileName.zip")
    .also {
      it.parentFile.mkdirs()
      it.createNewFile()
    }

  ZipOutputStream(backupFile.outputStream()).use { zipOutputStream ->
    backupFiles
      .flatMap { it.walkTopDown() }
      .fastFilter { !it.isDirectory }
      .fastFilter { it.absolutePath !in BACKUP_BLACKLIST }
      .fastFilter { it.exists() }
      .fastForEach { file ->
        d { "backup file $file" }
        val entry = ZipEntry(file.relativeTo(dirs.data).toString())
        zipOutputStream.putNextEntry(entry)
        file.inputStream().copyTo(zipOutputStream)
        zipOutputStream.closeEntry()
      }
  }

  val uri = FileProvider.getUriForFile(
    context,
    "${appConfig.packageName}.backupprovider",
    backupFile
  )
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "application/zip"
    data = uri
    putExtra(Intent.EXTRA_STREAM, uri)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
  }
  context.packageManager
    .queryIntentActivities(intent, PackageManager.MATCH_ALL)
    .fastMap { it.activityInfo.packageName }
    .fastDistinctBy { it }
    .fastForEach {
      context.grantUriPermission(
        it,
        uri,
        Intent.FLAG_GRANT_READ_URI_PERMISSION
      )
    }

  navigator.push(Intent.createChooser(intent, "Share File").asScreen())?.getOrThrow()
}

@Tag typealias restoreBackupResult = Unit
typealias restoreBackup = suspend () -> restoreBackupResult

@Provide suspend fun restoreBackup(
  context: Application,
  coroutineContexts: CoroutineContexts,
  dirs: AppDirs,
  logger: Logger = inject,
  navigator: Navigator,
  restartProcess: restartProcess,
): restoreBackupResult = withContext(coroutineContexts.io) {
  val uri = navigator.push(
    Intent.createChooser(
      Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "application/zip"
      },
      ""
    ).asScreen()
  )?.getOrNull()?.data?.data ?: return@withContext

  ZipInputStream(context.contentResolver.openInputStream(uri)!!).use { zipInputStream ->
    generateSequence { zipInputStream.nextEntry }
      .forEach { entry ->
        val file = dirs.data.resolve(entry.name)
        d { "restore file $file" }
        if (!file.exists()) {
          file.parentFile.mkdirs()
          file.createNewFile()
        }
        zipInputStream.copyTo(file.outputStream())
        zipInputStream.closeEntry()
      }
  }

  restartProcess()
}

private val BACKUP_BLACKLIST = listOf(
  "com.google.android.datatransport.events",
  "com.google.android.datatransport.events-journal"
)

@Tag typealias BackupFile = File

@Tag typealias BackupDestinationDir = File

@Provide object BackupFileProviders {
  @Provide fun backupPrefs(dirs: AppDirs): BackupFile = dirs.prefs

  @Provide fun backupDatabases(dirs: AppDirs): BackupFile = dirs.data.resolve("databases")

  @Provide fun backupSharedPrefs(dirs: AppDirs): BackupFile = dirs.data.resolve("shared_prefs")

  @Provide fun backupDestinationDir(dirs: AppDirs): BackupDestinationDir = dirs.data.resolve("files/backups")
}

@Provide @AndroidComponent class BackupFileProvider : FileProvider()

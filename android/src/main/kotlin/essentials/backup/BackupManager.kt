/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.backup

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

@Provide @ScopedService<AppScope> data class BackupDependencies(
  val backupDestinationDir: BackupDestinationDir,
  val backupFiles: List<BackupFile>,
  val dataDir: DataDir
)

suspend fun createBackup(scope: Scope<*> = inject) = withContext(coroutineContexts().io) {
  val dependencies = service<BackupDependencies>()
  val dateFormat = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss")
  val backupFileName =
    "${appConfig().packageName.replace(".", "_")}_${dateFormat.format(Date())}"

  val backupFile = dependencies.backupDestinationDir.resolve("$backupFileName.zip")
    .also {
      it.parentFile.mkdirs()
      it.createNewFile()
    }

  ZipOutputStream(backupFile.outputStream()).use { zipOutputStream ->
    dependencies.backupFiles
      .flatMap { it.walkTopDown() }
      .fastFilter { !it.isDirectory }
      .fastFilter { it.absolutePath !in BACKUP_BLACKLIST }
      .fastFilter { it.exists() }
      .fastForEach { file ->
        d { "backup file $file" }
        val entry = ZipEntry(file.relativeTo(dependencies.dataDir).toString())
        zipOutputStream.putNextEntry(entry)
        file.inputStream().copyTo(zipOutputStream)
        zipOutputStream.closeEntry()
      }
  }

  val uri = FileProvider.getUriForFile(
    appContext(),
    "${appConfig().packageName}.backupprovider",
    backupFile
  )
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "application/zip"
    data = uri
    putExtra(Intent.EXTRA_STREAM, uri)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
  }
  packageManager()
    .queryIntentActivities(intent, PackageManager.MATCH_ALL)
    .fastMap { it.activityInfo.packageName }
    .fastDistinctBy { it }
    .fastForEach {
      appContext().grantUriPermission(
        it,
        uri,
        Intent.FLAG_GRANT_READ_URI_PERMISSION
      )
    }

  navigator().push(
    Intent.createChooser(intent, "Share File")
      .asScreen()
  )?.getOrThrow()
}

suspend fun restoreBackup(scope: Scope<*> = inject) = withContext(coroutineContexts().io) {
  val uri = navigator().push(
    Intent.createChooser(
      Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "application/zip"
      },
      ""
    ).asScreen()
  )?.getOrNull()?.data?.data ?: return@withContext

  ZipInputStream(appContext().contentResolver.openInputStream(uri)!!).use { zipInputStream ->
    generateSequence { zipInputStream.nextEntry }
      .forEach { entry ->
        val file = service<BackupDependencies>().dataDir.resolve(entry.name)
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
  @Provide fun backupPrefs(prefsDir: PrefsDir): BackupFile = prefsDir

  @Provide fun backupDatabases(dataDir: DataDir): BackupFile = dataDir.resolve("databases")

  @Provide fun backupSharedPrefs(dataDir: DataDir): BackupFile = dataDir.resolve("shared_prefs")

  @Provide fun backupDestinationDir(dataDir: DataDir): BackupDestinationDir = dataDir.resolve("files/backups")
}

@Provide @AndroidComponent class BackupFileProvider : FileProvider()

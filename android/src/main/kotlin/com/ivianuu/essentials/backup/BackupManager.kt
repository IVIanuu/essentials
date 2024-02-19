/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import android.content.*
import android.content.pm.*
import android.icu.text.*
import androidx.core.content.*
import app.cash.quiver.extensions.*
import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.*
import java.io.*
import java.util.*
import java.util.zip.*

@Provide class BackupManager(
  private val appContext: AppContext,
  private val backupDir: BackupDir,
  private val backupFiles: List<BackupFile>,
  private val appConfig: AppConfig,
  private val contentResolver: ContentResolver,
  private val coroutineContexts: CoroutineContexts,
  private val dataDir: DataDir,
  private val logger: Logger,
  private val navigator: Navigator,
  private val packageManager: PackageManager,
  private val processRestarter: ProcessRestarter,
  private val scope: ScopedCoroutineScope<AppScope>
) {
  suspend fun createBackup(): Unit =
    withContext(scope.coroutineContext + coroutineContexts.io) {
      val dateFormat = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss")
      val backupFileName =
        "${appConfig.packageName.replace(".", "_")}_${dateFormat.format(Date())}"

      val backupFile = backupDir.resolve("$backupFileName.zip")
        .also {
          it.parentFile.mkdirs()
          it.createNewFile()
        }

      ZipOutputStream(backupFile.outputStream()).use { zipOutputStream ->
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
      }

      val uri = FileProvider.getUriForFile(
        appContext,
        "${appConfig.packageName}.backupprovider",
        backupFile
      )
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/zip"
        data = uri
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      packageManager
        .queryIntentActivities(intent, PackageManager.MATCH_ALL)
        .map { it.activityInfo.packageName }
        .distinct()
        .forEach {
          appContext.grantUriPermission(
            it,
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
          )
        }

      navigator.push(Intent.createChooser(intent, "Share File").asScreen())?.orThrow()
    }

  suspend fun restoreBackup() = withContext(scope.coroutineContext + coroutineContexts.io) {
    val uri = navigator.push(
      Intent.createChooser(
        Intent(Intent.ACTION_GET_CONTENT).apply {
          type = "application/zip"
        },
        ""
      ).asScreen()
    )?.getOrNull()?.data?.data ?: return@withContext

    ZipInputStream(contentResolver.openInputStream(uri)!!).use { zipInputStream ->
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

    }

    processRestarter()
  }
}

private val BACKUP_BLACKLIST = listOf(
  "com.google.android.datatransport.events",
  "com.google.android.datatransport.events-journal"
)

@Tag annotation class BackupFileTag {
  @Provide companion object {
    @Provide fun backupPrefs(prefsDir: PrefsDir): BackupFile = prefsDir

    @Provide fun backupDatabases(dataDir: DataDir): BackupFile = dataDir.resolve("databases")

    @Provide fun backupSharedPrefs(dataDir: DataDir): BackupFile = dataDir.resolve("shared_prefs")
  }
}
typealias BackupFile = @BackupFileTag File

@Tag annotation class BackupDirTag {
  @Provide companion object {
    @Provide fun backupDir(dataDir: DataDir): BackupDir = dataDir.resolve("files/backups")
  }
}
typealias BackupDir = @BackupDirTag File

@Provide @AndroidComponent class BackupFileProvider : FileProvider()

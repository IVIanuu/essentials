/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import android.content.ContentResolver
import android.content.Intent
import android.icu.text.SimpleDateFormat
import app.cash.quiver.extensions.orThrow
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.data.DataDir
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.util.ProcessRestarter
import com.ivianuu.essentials.ui.navigation.DefaultIntentScreen
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

@Provide class BackupManager(
  private val backupDir: BackupDir,
  private val backupFiles: List<BackupFile>,
  private val appConfig: AppConfig,
  private val contentResolver: ContentResolver,
  private val coroutineContexts: CoroutineContexts,
  private val dataDir: DataDir,
  private val logger: Logger,
  private val navigator: Navigator,
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

      val zipOutputStream = ZipOutputStream(backupFile.outputStream())

      backupFiles
        .flatMap { it.walkTopDown() }
        .filterNot { it.isDirectory }
        .filterNot { it.absolutePath in BACKUP_BLACKLIST }
        .filter { it.exists() }
        .forEach { file ->
          logger.log { "backup file $file" }
          val entry = ZipEntry(file.relativeTo(dataDir).toString())
          zipOutputStream.putNextEntry(entry)
          file.inputStream().copyTo(zipOutputStream)
          zipOutputStream.closeEntry()
        }

      zipOutputStream.close()

      navigator.push(ShareBackupFileScreen(backupFile.absolutePath))?.orThrow()
    }

  suspend fun restoreBackup() = withContext(scope.coroutineContext + coroutineContexts.io) {
    val uri = navigator.push(
      DefaultIntentScreen(
        Intent.createChooser(
          Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/zip"
          },
          ""
        )
      )
    )?.getOrNull()?.data?.data ?: return@withContext

    val zipInputStream = ZipInputStream(contentResolver.openInputStream(uri)!!)

    generateSequence { zipInputStream.nextEntry }
      .forEach { entry ->
        val file = dataDir.resolve(entry.name)
        logger.log { "restore file $file" }
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

private val BACKUP_BLACKLIST = listOf(
  "com.google.android.datatransport.events",
  "com.google.android.datatransport.events-journal"
)

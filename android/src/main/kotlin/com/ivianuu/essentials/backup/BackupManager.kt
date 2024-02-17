/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import android.content.*
import android.icu.text.*
import app.cash.quiver.extensions.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import java.util.*
import java.util.zip.*

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
      Intent.createChooser(
        Intent(Intent.ACTION_GET_CONTENT).apply {
          type = "application/zip"
        },
        ""
      ).asScreen()
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

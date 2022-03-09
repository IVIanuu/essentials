/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import android.content.*
import android.icu.text.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.processrestart.*
import com.ivianuu.essentials.ui.navigation.*
import kotlinx.coroutines.*
import java.util.*
import java.util.zip.*

fun interface CreateBackupUseCase : suspend () -> Result<Unit, Throwable>

@Provide fun createBackupUseCase(
  backupDir: BackupDir,
  backupFiles: List<BackupFile>,
  buildInfo: BuildInfo,
  context: IOContext,
  dataDir: DataDir,
  navigator: Navigator,
  scope: NamedCoroutineScope<AppScope>,
  L: Logger
) = CreateBackupUseCase {
  runCatching {
    withContext(scope.coroutineContext + context) {
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
          log { "backup file $file" }
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

fun interface RestoreBackupUseCase : suspend () -> Result<Unit, Throwable>

@Provide fun restoreBackupUseCase(
  contentResolver: ContentResolver,
  context: IOContext,
  dataDir: DataDir,
  navigator: Navigator,
  processRestarter: ProcessRestarter,
  scope: NamedCoroutineScope<AppScope>,
  L: Logger
) = RestoreBackupUseCase {
  runCatching {
    withContext(scope.coroutineContext + context) {
      val uri = navigator.push(
        Intent.createChooser(
          Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/zip"
          },
          ""
        ).toIntentKey()
      )?.getOrElse { null }?.data?.data ?: return@withContext

      val zipInputStream = ZipInputStream(contentResolver.openInputStream(uri)!!)

      generateSequence { zipInputStream.nextEntry }
        .forEach { entry ->
          val file = dataDir.resolve(entry.name)
          log { "restore file $file" }
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

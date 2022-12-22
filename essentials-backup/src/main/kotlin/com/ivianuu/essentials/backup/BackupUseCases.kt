/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import android.content.ContentResolver
import android.content.Intent
import android.icu.text.SimpleDateFormat
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.data.DataDir
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.processrestart.ProcessRestarter
import com.ivianuu.essentials.ui.navigation.DefaultIntentKey
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IOContext
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.withContext
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

fun interface CreateBackupUseCase : suspend () -> Result<Unit, Throwable>

context(Logger, NamedCoroutineScope<AppScope>) @Provide fun createBackupUseCase(
  backupDir: BackupDir,
  backupFiles: List<BackupFile>,
  buildInfo: BuildInfo,
  context: IOContext,
  dataDir: DataDir,
  navigator: Navigator
) = CreateBackupUseCase {
  catch {
    withContext(coroutineContext + context) {
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

context(Logger, NamedCoroutineScope<AppScope>) @Provide fun restoreBackupUseCase(
  contentResolver: ContentResolver,
  context: IOContext,
  dataDir: DataDir,
  navigator: Navigator,
  processRestarter: ProcessRestarter
) = RestoreBackupUseCase {
  catch {
    withContext(coroutineContext + context) {
      val uri = navigator.push(
        DefaultIntentKey(
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

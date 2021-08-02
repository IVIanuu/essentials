/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

@Provide fun createBackupUseCase(
  backupDir: BackupDir,
  backupFiles: Set<BackupFile>,
  buildInfo: BuildInfo,
  dataDir: DataDir,
  dispatcher: IODispatcher,
  logger: Logger,
  navigator: Navigator,
  scope: InjektCoroutineScope<AppScope>
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
          d { "backup file $file" }
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

@Provide fun restoreBackupUseCase(
  contentResolver: ContentResolver,
  dataDir: DataDir,
  dispatcher: IODispatcher,
  logger: Logger,
  navigator: Navigator,
  processRestarter: ProcessRestarter,
  scope: InjektCoroutineScope<AppScope>
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
          d { "restore file $file" }
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

/*
 * Copyright 2020 Manuel Wrage
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

import android.content.ContentResolver
import android.content.Intent
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.awaitAsync
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.processrestart.ProcessRestarter
import com.ivianuu.essentials.result.Result
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.essentials.util.ActivityResultLauncher
import com.ivianuu.injekt.Given
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

typealias BackupApplier = suspend () -> Result<Unit, Throwable>

@Given
fun backupApplier(
    @Given activityResultLauncher: ActivityResultLauncher,
    @Given contentResolver: ContentResolver,
    @Given ioDispatcher: IODispatcher,
    @Given globalScope: GlobalScope,
    @Given prefsDir: PrefsDir,
    @Given processRestarter: ProcessRestarter
): BackupApplier = {
    runKatching {
        globalScope.awaitAsync(ioDispatcher) {
            val uri = activityResultLauncher.startActivityForResult(
                Intent.createChooser(
                    Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "application/zip"
                    },
                    ""
                )
            ).data?.data ?: return@awaitAsync

            val buffer = ByteArray(8192)

            val zipInputStream = ZipInputStream(contentResolver.openInputStream(uri)!!.buffered())

            var entry: ZipEntry? = zipInputStream.nextEntry
            while (entry != null) {
                val file = File(prefsDir, entry.name)
                if (file.absolutePath in BACKUP_BLACKLIST) continue
                val dir = if (entry.isDirectory) file else file.parentFile
                if (!dir.isDirectory && !dir.mkdirs())
                    throw FileNotFoundException("Failed to ensure directory: " + dir.absolutePath)
                if (entry.isDirectory)
                    continue
                FileOutputStream(file).use { fileOutputStream ->
                    var count = zipInputStream.read(buffer)
                    while (count != -1) {
                        fileOutputStream.write(buffer, 0, count)
                        count = zipInputStream.read(buffer)
                    }
                }
                entry = zipInputStream.nextEntry
            }

            processRestarter()
        }
    }
}

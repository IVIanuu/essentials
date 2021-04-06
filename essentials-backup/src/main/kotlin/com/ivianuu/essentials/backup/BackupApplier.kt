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
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.data.DataDir
import com.ivianuu.essentials.processrestart.ProcessRestarter
import com.ivianuu.essentials.util.ActivityResultLauncher
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.withContext
import java.util.zip.ZipInputStream

typealias BackupApplier = suspend () -> Result<Unit, Throwable>

@Given
fun backupApplier(
    @Given activityResultLauncher: ActivityResultLauncher,
    @Given contentResolver: ContentResolver,
    @Given dataDir: DataDir,
    @Given ioDispatcher: IODispatcher,
    @Given logger: Logger,
    @Given processRestarter: ProcessRestarter,
    @Given scope: ScopeCoroutineScope<AppGivenScope>
): BackupApplier = {
    runCatching {
        withContext(scope.coroutineContext + ioDispatcher) {
            val uri = activityResultLauncher.startActivityForResult(
                Intent.createChooser(
                    Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "application/zip"
                    },
                    ""
                )
            ).data?.data ?: return@withContext

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

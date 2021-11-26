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

import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.FileProvider
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.ui.navigation.IntentKey
import com.ivianuu.essentials.ui.navigation.KeyIntentFactory
import com.ivianuu.injekt.Provide
import java.io.File

data class ShareBackupFileKey(val backupFilePath: String) :
  IntentKey

@Provide fun shareBackupFileKeyIntentFactory(
  context: AppContext,
  buildInfo: BuildInfo,
  packageManager: PackageManager
) = KeyIntentFactory<ShareBackupFileKey> { key ->
  val uri = FileProvider.getUriForFile(
    context,
    "${buildInfo.packageName}.backupprovider",
    File(key.backupFilePath)
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
      context.grantUriPermission(
        it,
        uri,
        Intent.FLAG_GRANT_READ_URI_PERMISSION
      )
    }

  Intent.createChooser(intent, "Share File")
}

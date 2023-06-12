/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.FileProvider
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ui.navigation.IntentKey
import com.ivianuu.essentials.ui.navigation.KeyIntentFactory
import com.ivianuu.injekt.Provide
import java.io.File

class ShareBackupFileKey(val backupFilePath: String) :
  IntentKey

@Provide fun shareBackupFileKeyIntentFactory(
  appContext: AppContext,
  appConfig: AppConfig
) = KeyIntentFactory<ShareBackupFileKey> { key ->
  val uri = FileProvider.getUriForFile(
    appContext,
    "${appConfig.packageName}.backupprovider",
    File(key.backupFilePath)
  )
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "application/zip"
    data = uri
    putExtra(Intent.EXTRA_STREAM, uri)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
  }
  appContext.packageManager
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

  Intent.createChooser(intent, "Share File")
}

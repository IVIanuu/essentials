/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import android.content.*
import android.content.pm.*
import androidx.core.content.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import java.io.*

class ShareBackupFileScreen(val backupFilePath: String) : IntentScreen

@Provide fun shareBackupFileKeyIntentFactory(
  appContext: AppContext,
  appConfig: AppConfig
) = ScreenIntentFactory<ShareBackupFileScreen> { key ->
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

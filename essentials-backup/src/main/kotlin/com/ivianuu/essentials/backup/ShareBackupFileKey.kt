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

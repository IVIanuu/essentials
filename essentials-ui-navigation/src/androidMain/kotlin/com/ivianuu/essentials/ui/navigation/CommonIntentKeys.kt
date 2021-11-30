/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.net.toUri
import com.ivianuu.injekt.Provide

data class DefaultIntentKey(val intent: Intent) : IntentKey

fun Intent.toIntentKey() = DefaultIntentKey(this)

@Provide val defaultIntentKeyIntentFactory = KeyIntentFactory<DefaultIntentKey> { it.intent }

data class AppInfoKey(val packageName: String) : IntentKey

@Provide val appInfoKeyIntentFactory = KeyIntentFactory<AppInfoKey> { key ->
  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
    this.data = "package:${key.packageName}".toUri()
  }
}

data class AppKey(val packageName: String) : IntentKey

@Provide fun appKeyIntentFactory(packageManager: PackageManager) = KeyIntentFactory<AppKey> { key ->
  packageManager.getLaunchIntentForPackage(key.packageName)!!
}

data class ShareKey(val text: String) : IntentKey

@Provide val shareKeyIntentFactory = KeyIntentFactory<ShareKey> { key ->
  Intent.createChooser(
    Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_TEXT, key.text)
    },
    ""
  )
}

data class UrlKey(val url: String) : IntentKey

@Provide val urlKeyIntentFactory = KeyIntentFactory<UrlKey> { key ->
  Intent(Intent.ACTION_VIEW).apply { this.data = key.url.toUri() }
}

fun PlayStoreAppDetailsKey(packageName: String) =
  UrlKey("https://play.google.com/store/apps/details?id=${packageName}")

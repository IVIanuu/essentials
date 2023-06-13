/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.net.toUri
import com.ivianuu.injekt.Provide

class DefaultIntentScreen(val intent: Intent) : IntentScreen

@Provide val defaultIntentKeyIntentFactory = KeyIntentFactory<DefaultIntentScreen> { it.intent }

class AppInfoScreen(val packageName: String) : IntentScreen

@Provide val appInfoKeyIntentFactory = KeyIntentFactory<AppInfoScreen> { key ->
  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
    this.data = "package:${key.packageName}".toUri()
  }
}

class AppScreen(val packageName: String) : IntentScreen

@Provide fun appKeyIntentFactory(packageManager: PackageManager) = KeyIntentFactory<AppScreen> { key ->
  packageManager.getLaunchIntentForPackage(key.packageName)!!
}

class ShareScreen(val text: String) : IntentScreen

@Provide val shareKeyIntentFactory = KeyIntentFactory<ShareScreen> { key ->
  Intent.createChooser(
    Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_TEXT, key.text)
    },
    ""
  )
}

class UrlScreen(val url: String) : IntentScreen

@Provide val urlKeyIntentFactory = KeyIntentFactory<UrlScreen> { key ->
  Intent(Intent.ACTION_VIEW).apply { this.data = key.url.toUri() }
}

fun PlayStoreAppDetailsKey(packageName: String) =
  UrlScreen("https://play.google.com/store/apps/details?id=${packageName}")

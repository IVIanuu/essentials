package com.ivianuu.essentials.ui.navigation

import android.content.*
import android.content.pm.*
import android.provider.*
import androidx.core.net.*
import com.ivianuu.injekt.*

class DefaultIntentScreen internal constructor(val intent: Intent) : IntentScreen {
  @Provide companion object {
    @Provide val intentFactory = ScreenIntentFactory<DefaultIntentScreen> { it.intent }
  }
}

fun Intent.asScreen(): IntentScreen = DefaultIntentScreen(this)

class AppInfoScreen(val packageName: String) : IntentScreen {
  @Provide companion object {
    @Provide val intentFactory = ScreenIntentFactory<AppInfoScreen> { screen ->
      Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        this.data = "package:${screen.packageName}".toUri()
      }
    }
  }
}

class AppScreen(val packageName: String) : IntentScreen {
  @Provide companion object {
    @Provide fun intentFactory(packageManager: PackageManager) = ScreenIntentFactory<AppScreen> { screen ->
      packageManager.getLaunchIntentForPackage(screen.packageName)!!
    }
  }
}

class ShareScreen(val text: String) : IntentScreen {
  @Provide companion object {
    @Provide val intentFactory = ScreenIntentFactory<ShareScreen> { key ->
      Intent.createChooser(
        Intent(Intent.ACTION_SEND).apply {
          type = "text/plain"
          putExtra(Intent.EXTRA_TEXT, key.text)
        },
        ""
      )
    }
  }
}

class UrlScreen(val url: String) : IntentScreen {
  @Provide companion object {
    @Provide val intentFactory = ScreenIntentFactory<UrlScreen> { screen ->
      Intent(Intent.ACTION_VIEW).apply { this.data = screen.url.toUri() }
    }
  }
}

fun PlayStoreAppDetailsKey(packageName: String) =
  UrlScreen("https://play.google.com/store/apps/details?id=${packageName}")

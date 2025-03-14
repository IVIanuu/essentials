package essentials.ui.navigation

import android.app.Application
import android.content.*
import android.content.pm.*
import android.provider.*
import androidx.core.net.*
import injekt.*

class DefaultIntentScreen internal constructor(val intent: Intent) : IntentScreen {
  @Provide companion object {
    @Provide fun intent(screen: DefaultIntentScreen): ScreenIntent<DefaultIntentScreen> = screen.intent
  }
}

fun Intent.asScreen(): IntentScreen = DefaultIntentScreen(this)

class AppInfoScreen(val packageName: String) : IntentScreen {
  @Provide companion object {
    @Provide fun intent(screen: AppInfoScreen): ScreenIntent<AppInfoScreen> =
      Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        this.data = "package:${screen.packageName}".toUri()
      }
  }
}

class AppScreen(val packageName: String) : IntentScreen {
  @Provide companion object {
    @Provide fun intent(
      application: Application,
      screen: AppScreen
    ): ScreenIntent<AppScreen> = application.packageManager
      .getLaunchIntentForPackage(screen.packageName)!!
  }
}

class ShareScreen(val text: String) : IntentScreen {
  @Provide companion object {
    @Provide fun intent(screen: ShareScreen): ScreenIntent<ShareScreen> = Intent.createChooser(
      Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, screen.text)
      },
      ""
    )
  }
}

class UrlScreen(val url: String) : IntentScreen {
  @Provide companion object {
    @Provide fun intent(screen: UrlScreen): ScreenIntent<UrlScreen> =
      Intent(Intent.ACTION_VIEW).apply { this.data = screen.url.toUri() }
  }
}

fun PlayStoreAppDetailsKey(packageName: String) =
  UrlScreen("https://play.google.com/store/apps/details?id=${packageName}")

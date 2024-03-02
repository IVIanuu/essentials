package com.ivianuu.essentials.about

import android.content.*
import android.net.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import compose.icons.*
import compose.icons.fontawesomeicons.*
import compose.icons.fontawesomeicons.brands.*

class AboutScreen(
  val donationScreen: Screen<*>?,
  val privacyPolicyUrl: String?
) : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      appConfig: AppConfig,
      navigator: Navigator,
      screen: AboutScreen
    ) = Ui<AboutScreen> {
      ScreenScaffold(topBar = { AppBar { Text("About") } }) {
        VerticalList {
          item {
            ListItem(
              leading = { Icon(Icons.Default.Info, null) },
              title = { Text("Version") },
              subtitle = { Text(appConfig.versionName) }
            )
          }

          item {
            ListItem(
              onClick = scopedAction { navigator.push(PlayStoreAppDetailsKey(appConfig.packageName)) },
              leading = { Icon(Icons.Default.Star, null) },
              title = { Text("Rate") },
              subtitle = { Text("I'll be happy if you give me 5 stars") }
            )
          }

          if (screen.donationScreen != null)
            item {
              ListItem(
                onClick = scopedAction { navigator.push(screen.donationScreen) },
                leading = { Icon(Icons.Default.Favorite, null) },
                title = { Text("Donate") }
              )
            }

          item {
            ListItem(
              onClick = scopedAction {
                navigator.push(UrlScreen("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
              },
              leading = { Icon(Icons.Default.ShoppingBag, null) },
              title = { Text("More apps") },
              subtitle = { Text("Check out my other apps on Google Play") }
            )
          }

          item {
            ListItem(
              onClick = scopedAction {
                navigator.push(UrlScreen("https://www.reddit.com/r/manuelwrageapps"))
              },
              leading = {
                Icon(
                  imageVector = FontAwesomeIcons.Brands.RedditAlien,
                  modifier = Modifier.size(24.dp),
                  contentDescription = null
                )
              },
              title = { Text("Reddit") },
              subtitle = { Text("If you need help or have questions, my subreddit is a good place to go") }
            )
          }

          item {
            ListItem(
              onClick = scopedAction { navigator.push(UrlScreen("https://github.com/IVIanuu")) },
              leading = {
                Icon(
                  imageVector = FontAwesomeIcons.Brands.Github,
                  modifier = Modifier.size(24.dp),
                  contentDescription = null
                )
              },
              title = { Text("Github") },
              subtitle = { Text("Check out my work on Github") }
            )
          }

          item {
            ListItem(
              onClick = scopedAction { navigator.push(UrlScreen("https://twitter.com/IVIanuu")) },
              leading = {
                Icon(
                  imageVector = FontAwesomeIcons.Brands.Twitter,
                  modifier = Modifier.size(24.dp),
                  contentDescription = null
                )
              },
              title = { Text("Twitter") },
              subtitle = { Text("Follow me on Twitter") }
            )
          }

          item {
            val email = "ivianuu@gmail.com"
            ListItem(
              onClick = scopedAction {
                navigator.push(
                  Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                    putExtra(
                      Intent.EXTRA_SUBJECT,
                      "Feedback for ${appConfig.appName} ${appConfig.versionName}"
                    )
                  }
                    .asScreen()
                )
              },
              leading = { Icon(Icons.Default.Email, null) },
              title = { Text("Send feedback") },
              subtitle = { Text(email) }
            )
          }

          if (screen.privacyPolicyUrl != null)
            item {
              ListItem(
                onClick = scopedAction { navigator.push(UrlScreen(screen.privacyPolicyUrl)) },
                leading = { Icon(Icons.Default.Policy, null) },
                title = { Text("Privacy policy") }
              )
            }
        }
      }
    }
  }
}

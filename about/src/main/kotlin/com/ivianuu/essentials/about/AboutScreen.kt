/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.about

import android.content.*
import android.net.*
import androidx.compose.material.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.donation.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

class AboutScreen(val privacyPolicyUrl: String? = null) : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      appConfig: AppConfig,
      donations: (() -> List<Donation>)? = null,
      navigator: Navigator,
      screen: AboutScreen
    ) = Ui<AboutScreen, Unit> {
      ScreenScaffold(topBar = { AppBar { Text("About") } }) {
        VerticalList {
          item {
            ListItem(
              leading = { Icon(painterResource(R.drawable.ic_info), null) },
              title = { Text("Version") },
              subtitle = { Text(appConfig.versionName) }
            )
          }

          item {
            ListItem(
              onClick = action { navigator.push(PlayStoreAppDetailsKey(appConfig.packageName)) },
              leading = { Icon(painterResource(com.ivianuu.essentials.android.R.drawable.ic_star), null) },
              title = { Text("Rate") },
              subtitle = { Text("I'll be happy if you give me 5 stars") }
            )
          }

          if (donations != null)
            item {
              ListItem(
                onClick = action { navigator.push(DonationScreen()) },
                leading = { Icon(painterResource(R.drawable.ic_favorite), null) },
                title = { Text("Donate") }
              )
            }

          item {
            ListItem(
              onClick = action {
                navigator.push(UrlScreen("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
              },
              leading = { Icon(painterResource(R.drawable.ic_google_play), null) },
              title = { Text("More apps") },
              subtitle = { Text("Check out my other apps on Google Play") }
            )
          }

          item {
            ListItem(
              onClick = action {
                navigator.push(UrlScreen("https://www.reddit.com/r/manuelwrageapps"))
              },
              leading = { Icon(painterResource(R.drawable.ic_reddit), null) },
              title = { Text("Reddit") },
              subtitle = { Text("If you need help or have questions, my subreddit is a good place to go") }
            )
          }

          item {
            ListItem(
              onClick = action { navigator.push(UrlScreen("https://github.com/IVIanuu")) },
              leading = { Icon(painterResource(R.drawable.ic_github), null) },
              title = { Text("Github") },
              subtitle = { Text("Check out my work on Github") }
            )
          }

          item {
            ListItem(
              onClick = action { navigator.push(UrlScreen("https://twitter.com/IVIanuu")) },
              leading = { Icon(painterResource(R.drawable.ic_twitter), null) },
              title = { Text("Twitter") },
              subtitle = { Text("Follow me on Twitter") }
            )
          }

          item {
            val email = "ivianuu@gmail.com"
            ListItem(
              onClick = action {
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
              leading = { Icon(painterResource(R.drawable.ic_email), null) },
              title = { Text("Send feedback") },
              subtitle = { Text(email) }
            )
          }

          if (screen.privacyPolicyUrl != null)
            item {
              ListItem(
                onClick = action { navigator.push(UrlScreen(screen.privacyPolicyUrl)) },
                leading = { Icon(painterResource(R.drawable.ic_policy), null) },
                title = { Text("Privacy policy") }
              )
            }
        }
      }
    }
  }
}

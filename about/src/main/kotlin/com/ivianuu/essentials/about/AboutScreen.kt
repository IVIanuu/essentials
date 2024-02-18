/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.about

import android.content.*
import android.net.*
import androidx.compose.material.*
import androidx.compose.ui.*
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
      ScreenScaffold(topBar = { AppBar { Text(stringResource(R.string.about_title)) } }) {
        VerticalList {
          item {
            ListItem(
              leading = { Icon(painterResource(R.drawable.ic_info), null) },
              title = { Text(stringResource(R.string.about_version)) },
              subtitle = { Text(appConfig.versionName) }
            )
          }

          item {
            ListItem(
              onClick = action { navigator.push(PlayStoreAppDetailsKey(appConfig.packageName)) },
              leading = { Icon(painterResource(com.ivianuu.essentials.android.R.drawable.ic_star), null) },
              title = { Text(stringResource(R.string.about_rate)) },
              subtitle = { Text(stringResource(R.string.about_rate_desc)) }
            )
          }

          if (donations != null)
            item {
              ListItem(
                onClick = action { navigator.push(DonationScreen()) },
                leading = { Icon(painterResource(R.drawable.ic_favorite), null) },
                title = { Text(stringResource(R.string.about_donate)) }
              )
            }

          item {
            ListItem(
              onClick = action {
                navigator.push(UrlScreen("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
              },
              leading = { Icon(painterResource(R.drawable.ic_google_play), null) },
              title = { Text(stringResource(R.string.about_more_apps)) },
              subtitle = { Text(stringResource(R.string.about_more_apps_desc)) }
            )
          }

          item {
            ListItem(
              onClick = action {
                navigator.push(UrlScreen("https://www.reddit.com/r/manuelwrageapps"))
              },
              leading = { Icon(painterResource(R.drawable.ic_reddit), null) },
              title = { Text(stringResource(R.string.about_reddit)) },
              subtitle = { Text(stringResource(R.string.about_reddit_desc)) }
            )
          }

          item {
            ListItem(
              onClick = action { navigator.push(UrlScreen("https://github.com/IVIanuu")) },
              leading = { Icon(painterResource(R.drawable.ic_github), null) },
              title = { Text(stringResource(R.string.about_github)) },
              subtitle = { Text(stringResource(R.string.about_github_desc)) }
            )
          }

          item {
            ListItem(
              onClick = action { navigator.push(UrlScreen("https://twitter.com/IVIanuu")) },
              leading = { Icon(painterResource(R.drawable.ic_twitter), null) },
              title = { Text(stringResource(R.string.about_twitter)) },
              subtitle = { Text(stringResource(R.string.about_twitter_desc)) }
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
              title = { Text(stringResource(R.string.about_feedback)) },
              subtitle = { Text(email) }
            )
          }

          if (screen.privacyPolicyUrl != null)
            item {
              ListItem(
                onClick = action { navigator.push(UrlScreen(screen.privacyPolicyUrl)) },
                leading = { Icon(painterResource(R.drawable.ic_policy), null) },
                title = { Text(stringResource(R.string.about_privacy_policy)) }
              )
            }
        }
      }
    }
  }
}

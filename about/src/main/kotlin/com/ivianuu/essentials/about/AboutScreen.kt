/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.about

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.donation.Donation
import com.ivianuu.essentials.donation.DonationScreen
import com.ivianuu.essentials.rate.DeveloperEmail
import com.ivianuu.essentials.rate.FeedbackMailScreen
import com.ivianuu.essentials.rate.RateUseCases
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.navigation.Presenter
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.UrlScreen
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide

class AboutScreen : Screen<Unit>

@Provide fun aboutUi(
  appConfig: AppConfig,
  privacyPolicyUrl: PrivacyPolicyUrl? = null,
  donations: (() -> List<Donation>)? = null,
  email: DeveloperEmail,
  navigator: Navigator,
  rateUseCases: RateUseCases
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
          modifier = Modifier.clickable(onClick = action { rateUseCases.rateOnPlay() }),
          leading = { Icon(painterResource(com.ivianuu.essentials.android.R.drawable.ic_star), null) },
          title = { Text(stringResource(R.string.about_rate)) },
          subtitle = { Text(stringResource(R.string.about_rate_desc)) }
        )
      }

      if (donations != null)
        item {
          ListItem(
            modifier = Modifier.clickable(onClick = action { navigator.push(DonationScreen()) }),
            leading = { Icon(painterResource(R.drawable.ic_favorite), null) },
            title = { Text(stringResource(R.string.about_donate)) }
          )
        }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = action {
            navigator.push(UrlScreen("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
          }),
          leading = { Icon(painterResource(R.drawable.ic_google_play), null) },
          title = { Text(stringResource(R.string.about_more_apps)) },
          subtitle = { Text(stringResource(R.string.about_more_apps_desc)) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = action {
            navigator.push(UrlScreen("https://www.reddit.com/r/manuelwrageapps"))
          }),
          leading = { Icon(painterResource(R.drawable.ic_reddit), null) },
          title = { Text(stringResource(R.string.about_reddit)) },
          subtitle = { Text(stringResource(R.string.about_reddit_desc)) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = action {
            navigator.push(UrlScreen("https://github.com/IVIanuu"))
          }),
          leading = { Icon(painterResource(R.drawable.ic_github), null) },
          title = { Text(stringResource(R.string.about_github)) },
          subtitle = { Text(stringResource(R.string.about_github_desc)) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = action {
            navigator.push(UrlScreen("https://twitter.com/IVIanuu"))
          }),
          leading = { Icon(painterResource(R.drawable.ic_twitter), null) },
          title = { Text(stringResource(R.string.about_twitter)) },
          subtitle = { Text(stringResource(R.string.about_twitter_desc)) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = action {
            navigator.push(FeedbackMailScreen)
          }),
          leading = { Icon(painterResource(R.drawable.ic_email), null) },
          title = { Text(stringResource(R.string.about_feedback)) },
          subtitle = { Text(email.value) }
        )
      }

      if (privacyPolicyUrl != null)
        item {
          ListItem(
            modifier = Modifier.clickable(onClick = action {
              navigator.push(UrlScreen(privacyPolicyUrl.value))
            }),
            leading = { Icon(painterResource(R.drawable.ic_policy), null) },
            title = { Text(stringResource(R.string.about_privacy_policy)) }
          )
        }
    }
  }
}

@JvmInline value class PrivacyPolicyUrl(val value: String)

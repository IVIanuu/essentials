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

@Provide val aboutUi = Ui<AboutScreen, AboutState> { state ->
  ScreenScaffold(topBar = { AppBar { Text(stringResource(R.string.es_about_title)) } }) {
    VerticalList {
      item {
        ListItem(
          leading = { Icon(painterResource(R.drawable.es_ic_info), null) },
          title = { Text(stringResource(R.string.es_about_version)) },
          subtitle = { Text(state.version) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = state.rate),
          leading = { Icon(painterResource(R.drawable.es_ic_star), null) },
          title = { Text(stringResource(R.string.es_about_rate)) },
          subtitle = { Text(stringResource(R.string.es_about_rate_desc)) }
        )
      }

      if (state.showDonate)
        item {
          ListItem(
            modifier = Modifier.clickable(onClick = state.donate),
            leading = { Icon(painterResource(R.drawable.es_ic_favorite), null) },
            title = { Text(stringResource(R.string.es_about_donate)) }
          )
        }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = state.openMoreApps),
          leading = { Icon(painterResource(R.drawable.es_ic_google_play), null) },
          title = { Text(stringResource(R.string.es_about_more_apps)) },
          subtitle = { Text(stringResource(R.string.es_about_more_apps_desc)) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = state.openRedditPage),
          leading = { Icon(painterResource(R.drawable.es_ic_reddit), null) },
          title = { Text(stringResource(R.string.es_about_reddit)) },
          subtitle = { Text(stringResource(R.string.es_about_reddit_desc)) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = state.openGithubPage),
          leading = { Icon(painterResource(R.drawable.es_ic_github), null) },
          title = { Text(stringResource(R.string.es_about_github)) },
          subtitle = { Text(stringResource(R.string.es_about_github_desc)) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = state.openTwitterPage),
          leading = { Icon(painterResource(R.drawable.es_ic_twitter), null) },
          title = { Text(stringResource(R.string.es_about_twitter)) },
          subtitle = { Text(stringResource(R.string.es_about_twitter_desc)) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = state.sendMail),
          leading = { Icon(painterResource(R.drawable.es_ic_email), null) },
          title = { Text(stringResource(R.string.es_about_feedback)) },
          subtitle = { Text(state.email.value) }
        )
      }

      if (state.privacyPolicyUrl != null)
        item {
          ListItem(
            modifier = Modifier.clickable(onClick = state.openPrivacyPolicy),
            leading = { Icon(painterResource(R.drawable.es_ic_policy), null) },
            title = { Text(stringResource(R.string.es_about_privacy_policy)) }
          )
        }
    }
  }
}

data class AboutState(
  val version: String,
  val email: DeveloperEmail,
  val privacyPolicyUrl: PrivacyPolicyUrl?,
  val showDonate: Boolean,
  val donate: () -> Unit,
  val rate: () -> Unit,
  val openMoreApps: () -> Unit,
  val openRedditPage: () -> Unit,
  val openGithubPage: () -> Unit,
  val openTwitterPage: () -> Unit,
  val openPrivacyPolicy: () -> Unit,
  val sendMail: () -> Unit
)

@JvmInline value class PrivacyPolicyUrl(val value: String)

@Provide fun aboutPresenter(
  appConfig: AppConfig,
  privacyPolicyUrl: PrivacyPolicyUrl? = null,
  donations: (() -> List<Donation>)? = null,
  email: DeveloperEmail,
  navigator: Navigator,
  rateUseCases: RateUseCases
) = Presenter {
  AboutState(
    version = appConfig.versionName,
    email = email,
    privacyPolicyUrl = privacyPolicyUrl,
    showDonate = donations != null,
    donate = action { navigator.push(DonationScreen()) },
    rate = action { rateUseCases.rateOnPlay() },
    openMoreApps = action {
      navigator.push(UrlScreen("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
    },
    openRedditPage = action { navigator.push(UrlScreen("https://www.reddit.com/r/manuelwrageapps")) },
    openGithubPage = action { navigator.push(UrlScreen("https://github.com/IVIanuu")) },
    openTwitterPage = action { navigator.push(UrlScreen("https://twitter.com/IVIanuu")) },
    openPrivacyPolicy = action { navigator.push(UrlScreen(privacyPolicyUrl!!.value)) },
    sendMail = action { navigator.push(FeedbackMailScreen) }
  )
}

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.about

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
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
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.UrlScreen
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide

class AboutScreen : Screen<Unit>

@Provide val aboutUi = Ui<AboutScreen, AboutModel> { model ->
  Scaffold(topBar = { AppBar { Text(R.string.es_about_title) } }) {
    VerticalList {
      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_info) },
          title = { Text(R.string.es_about_version) },
          subtitle = { Text(model.version) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = model.rate),
          leading = { Icon(R.drawable.es_ic_star) },
          title = { Text(R.string.es_about_rate) },
          subtitle = { Text(R.string.es_about_rate_desc) }
        )
      }

      if (model.showDonate)
        item {
          ListItem(
            modifier = Modifier.clickable(onClick = model.donate),
            leading = { Icon(R.drawable.es_ic_favorite) },
            title = { Text(R.string.es_about_donate) }
          )
        }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = model.openMoreApps),
          leading = { Icon(R.drawable.es_ic_google_play) },
          title = { Text(R.string.es_about_more_apps) },
          subtitle = { Text(R.string.es_about_more_apps_desc) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = model.openRedditPage),
          leading = { Icon(R.drawable.es_ic_reddit) },
          title = { Text(R.string.es_about_reddit) },
          subtitle = { Text(R.string.es_about_reddit_desc) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = model.openGithubPage),
          leading = { Icon(R.drawable.es_ic_github) },
          title = { Text(R.string.es_about_github) },
          subtitle = { Text(R.string.es_about_github_desc) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = model.openTwitterPage),
          leading = { Icon(R.drawable.es_ic_twitter) },
          title = { Text(R.string.es_about_twitter) },
          subtitle = { Text(R.string.es_about_twitter_desc) }
        )
      }

      item {
        ListItem(
          modifier = Modifier.clickable(onClick = model.sendMail),
          leading = { Icon(R.drawable.es_ic_email) },
          title = { Text(R.string.es_about_feedback) },
          subtitle = { Text(model.email.value) }
        )
      }

      if (model.privacyPolicyUrl != null)
        item {
          ListItem(
            modifier = Modifier.clickable(onClick = model.openPrivacyPolicy),
            leading = { Icon(R.drawable.es_ic_policy) },
            title = { Text(R.string.es_about_privacy_policy) }
          )
        }
    }
  }
}

data class AboutModel(
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

@Provide fun aboutModel(
  appConfig: AppConfig,
  privacyPolicyUrl: PrivacyPolicyUrl? = null,
  donations: (() -> List<Donation>)? = null,
  email: DeveloperEmail,
  navigator: Navigator,
  rateUseCases: RateUseCases
) = Model {
  AboutModel(
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

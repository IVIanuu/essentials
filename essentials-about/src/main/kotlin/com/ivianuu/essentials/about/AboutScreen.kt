/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.about

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.Strings
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.donation.Donation
import com.ivianuu.essentials.donation.DonationScreen
import com.ivianuu.essentials.license.LicenseScreen
import com.ivianuu.essentials.rate.DeveloperEmail
import com.ivianuu.essentials.rate.FeedbackMailScreen
import com.ivianuu.essentials.rate.RateUseCases
import com.ivianuu.essentials.stringKeyOf
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.UrlScreen
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.web.ui.WebScreen
import com.ivianuu.injekt.Provide

class AboutScreen : Screen<Unit>

@Provide val aboutUi = Ui<AboutScreen, AboutModel> { model ->
  SimpleListScreen("About") {
    item {
      ListItem(
        leading = { Icon(R.drawable.es_ic_info) },
        title = { Text("Version") },
        subtitle = { Text(model.version) }
      )
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = model.rate),
        leading = { Icon(R.drawable.es_ic_star) },
        title = { Text(Strings_Rate) },
        subtitle = { Text(Strings_RateDesc) }
      )
    }

    if (model.showDonate) {
      item {
        ListItem(
          modifier = Modifier.clickable(onClick = model.donate),
          leading = { Icon(R.drawable.es_ic_favorite) },
          title = { Text(Strings_Donate) }
        )
      }
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = model.openMoreApps),
        leading = { Icon(R.drawable.es_ic_google_play) },
        title = { Text(Strings_MoreApps) },
        subtitle = { Text(Strings_MoreAppsDesc) }
      )
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = model.openRedditPage),
        leading = { Icon(R.drawable.es_ic_reddit) },
        title = { Text(Strings_Reddit) },
        subtitle = { Text(Strings_RedditDesc) }
      )
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = model.openGithubPage),
        leading = { Icon(R.drawable.es_ic_github) },
        title = { Text(Strings_Github) },
        subtitle = { Text(Strings_GithubDesc) }
      )
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = model.openTwitterPage),
        leading = { Icon(R.drawable.es_ic_twitter) },
        title = { Text(Strings_Twitter) },
        subtitle = { Text(Strings_TwitterDesc) }
      )
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = model.sendMail),
        leading = { Icon(R.drawable.es_ic_email) },
        title = { Text(Strings_Feedback) },
        subtitle = { Text(model.email.value) }
      )
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = model.openLicenses),
        leading = { Icon(R.drawable.es_ic_assignment) },
        title = { Text(R.string.es_licenses_title) }
      )
    }

    if (model.privacyPolicyUrl != null) {
      item {
        ListItem(
          modifier = Modifier.clickable(onClick = model.openPrivacyPolicy),
          leading = { Icon(R.drawable.es_ic_policy) },
          title = { Text("Privacy policy") }
        )
      }
    }
  }
}

val Strings_Donate = stringKeyOf { "Support development" }
val Strings_Rate = stringKeyOf { "Rate" }
val Strings_RateDesc = stringKeyOf { "I'll be happy if you give me 5 stars" }
val Strings_MoreApps = stringKeyOf { "More apps" }
val Strings_MoreAppsDesc = stringKeyOf { "Check out my other apps on Google Play" }
val Strings_Reddit = stringKeyOf { "Reddit" }
val Strings_RedditDesc = stringKeyOf { "If you need help or have questions, my subreddit is a good place to go" }
val Strings_Github = stringKeyOf { "Github" }
val Strings_GithubDesc = stringKeyOf { "Check out my work on Github" }
val Strings_Feedback = stringKeyOf { "Send feedback" }
val Strings_Twitter = stringKeyOf { "Twitter" }
val Strings_TwitterDesc = stringKeyOf { "Follow me on Twitter" }

data class AboutModel(
  val version: String,
  val email: DeveloperEmail,
  val privacyPolicyUrl: PrivacyPolicyUrl?,
  val showDonate: Boolean,
  val donate: () -> Unit,
  val rate: () -> Unit,
  val openLicenses: () -> Unit,
  val openMoreApps: () -> Unit,
  val openRedditPage: () -> Unit,
  val openGithubPage: () -> Unit,
  val openTwitterPage: () -> Unit,
  val openPrivacyPolicy: () -> Unit,
  val sendMail: () -> Unit
)

@Provide fun aboutModel(
  appConfig: AppConfig,
  privacyPolicyUrl: PrivacyPolicyUrl? = null,
  donations: (() -> List<Donation>)? = null,
  email: DeveloperEmail,
  navigator: Navigator,
  rateUseCases: RateUseCases,
  strings: Strings
) = Model {
  AboutModel(
    version = appConfig.versionName,
    email = email,
    privacyPolicyUrl = privacyPolicyUrl,
    showDonate = donations != null,
    donate = action { navigator.push(DonationScreen()) },
    openLicenses = action { navigator.push(LicenseScreen()) },
    rate = action { rateUseCases.rateOnPlay() },
    openMoreApps = action {
      navigator.push(UrlScreen("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
    },
    openRedditPage = action { navigator.push(UrlScreen("https://www.reddit.com/r/manuelwrageapps")) },
    openGithubPage = action { navigator.push(UrlScreen("https://github.com/IVIanuu")) },
    openTwitterPage = action { navigator.push(UrlScreen("https://twitter.com/IVIanuu")) },
    openPrivacyPolicy = action {
      navigator.push(WebScreen("Privacy policy", privacyPolicyUrl!!.value))
    },
    sendMail = action { navigator.push(FeedbackMailScreen) }
  )
}

@JvmInline value class PrivacyPolicyUrl(val value: String)

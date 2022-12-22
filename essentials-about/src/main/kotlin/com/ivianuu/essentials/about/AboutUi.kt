/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.about

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.donation.Donation
import com.ivianuu.essentials.donation.DonationKey
import com.ivianuu.essentials.license.ui.LicenseKey
import com.ivianuu.essentials.rate.domain.RateOnPlayUseCase
import com.ivianuu.essentials.rate.ui.DeveloperEmail
import com.ivianuu.essentials.rate.ui.FeedbackMailKey
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.web.ui.WebKey
import com.ivianuu.injekt.Provide

object AboutKey : Key<Unit>

@Provide val aboutUi = ModelKeyUi<AboutKey, AboutModel> {
  SimpleListScreen(R.string.es_about_title) {
    item {
      ListItem(
        leading = { Icon(R.drawable.es_ic_info) },
        title = { Text(R.string.es_about_version) },
        subtitle = { Text(version) }
      )
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = rate),
        leading = { Icon(R.drawable.es_ic_star) },
        title = { Text(R.string.es_about_rate) },
        subtitle = { Text(R.string.es_about_rate_desc) }
      )
    }

    if (showDonate) {
      item {
        ListItem(
          modifier = Modifier.clickable(onClick = donate),
          leading = { Icon(R.drawable.es_ic_favorite) },
          title = { Text(R.string.es_about_donate) }
        )
      }
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = openMoreApps),
        leading = { Icon(R.drawable.es_ic_google_play) },
        title = { Text(R.string.es_about_more_apps) },
        subtitle = { Text(R.string.es_about_more_apps_desc) }
      )
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = openRedditPage),
        leading = { Icon(R.drawable.es_ic_reddit) },
        title = { Text(R.string.es_about_reddit) },
        subtitle = { Text(R.string.es_about_reddit_desc) }
      )
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = openGithubPage),
        leading = { Icon(R.drawable.es_ic_github) },
        title = { Text(R.string.es_about_github) },
        subtitle = { Text(R.string.es_about_github_desc) }
      )
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = openTwitterPage),
        leading = { Icon(R.drawable.es_ic_twitter) },
        title = { Text(R.string.es_about_twitter) },
        subtitle = { Text(R.string.es_about_twitter_desc) }
      )
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = sendMail),
        leading = { Icon(R.drawable.es_ic_email) },
        title = { Text(R.string.es_about_feedback) },
        subtitle = { Text(email.value) }
      )
    }

    item {
      ListItem(
        modifier = Modifier.clickable(onClick = openLicenses),
        leading = { Icon(R.drawable.es_ic_assignment) },
        title = { Text(R.string.es_licenses_title) }
      )
    }

    if (privacyPolicyUrl != null) {
      item {
        ListItem(
          modifier = Modifier.clickable(onClick = openPrivacyPolicy),
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
  val openLicenses: () -> Unit,
  val openMoreApps: () -> Unit,
  val openRedditPage: () -> Unit,
  val openGithubPage: () -> Unit,
  val openTwitterPage: () -> Unit,
  val openPrivacyPolicy: () -> Unit,
  val sendMail: () -> Unit
)

@JvmInline value class PrivacyPolicyUrl(val value: String)

context(KeyUiContext<AboutKey>, ResourceProvider) @Provide fun aboutModel(
  buildInfo: BuildInfo,
  privacyPolicyUrl: PrivacyPolicyUrl? = null,
  donations: (() -> List<Donation>)? = null,
  email: DeveloperEmail,
  rateOnPlayUseCase: RateOnPlayUseCase
) = Model {
  AboutModel(
    version = buildInfo.versionName,
    email = email,
    privacyPolicyUrl = privacyPolicyUrl,
    showDonate = donations != null,
    donate = action { navigator.push(DonationKey) },
    openLicenses = action { navigator.push(LicenseKey) },
    rate = action { rateOnPlayUseCase() },
    openMoreApps = action {
      navigator.push(UrlKey("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
    },
    openRedditPage = action { navigator.push(UrlKey("https://www.reddit.com/r/manuelwrageapps")) },
    openGithubPage = action { navigator.push(UrlKey("https://github.com/IVIanuu")) },
    openTwitterPage = action { navigator.push(UrlKey("https://twitter.com/IVIanuu")) },
    openPrivacyPolicy = action {
      navigator.push(
        WebKey(
          loadResource(R.string.es_about_privacy_policy),
          privacyPolicyUrl!!.value
        )
      )
    },
    sendMail = action { navigator.push(FeedbackMailKey) }
  )
}

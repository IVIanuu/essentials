/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.about

import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.ui.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.donation.*
import com.ivianuu.essentials.license.ui.*
import com.ivianuu.essentials.rate.domain.*
import com.ivianuu.essentials.rate.ui.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.web.ui.*
import com.ivianuu.injekt.*

object AboutKey : Key<Unit>

@Provide val aboutUi = ModelKeyUi<AboutKey, AboutModel> {
  SimpleListScreen(R.string.es_about_title) {
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

    if (model.showDonate) {
      item {
        ListItem(
          modifier = Modifier.clickable(onClick = model.donate),
          leading = { Icon(R.drawable.es_ic_favorite) },
          title = { Text(R.string.es_about_donate) }
        )
      }
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

@Provide fun aboutModel(
  buildInfo: BuildInfo,
  privacyPolicyUrl: PrivacyPolicyUrl? = null,
  donations: (() -> List<Donation>)? = null,
  email: DeveloperEmail,
  rateOnPlayUseCase: RateOnPlayUseCase,
  RP: ResourceProvider,
  ctx: KeyUiContext<AboutKey>
) = Model {
  AboutModel(
    version = buildInfo.versionName,
    email = email,
    privacyPolicyUrl = privacyPolicyUrl,
    showDonate = donations != null,
    donate = action { ctx.navigator.push(DonationKey) },
    openLicenses = action { ctx.navigator.push(LicenseKey) },
    rate = action { rateOnPlayUseCase() },
    openMoreApps = action {
      ctx.navigator.push(UrlKey("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
    },
    openRedditPage = action {
      ctx.navigator.push(UrlKey("https://www.reddit.com/r/manuelwrageapps"))
    },
    openGithubPage = action {
      ctx.navigator.push(UrlKey("https://github.com/IVIanuu"))
    },
    openTwitterPage = action {
      ctx.navigator.push(UrlKey("https://twitter.com/IVIanuu"))
    },
    openPrivacyPolicy = action {
      ctx.navigator.push(
        WebKey(
          loadResource(R.string.es_about_privacy_policy),
          privacyPolicyUrl!!.value
        )
      )
    },
    sendMail = action {
      ctx.navigator.push(FeedbackMailKey)
    }
  )
}

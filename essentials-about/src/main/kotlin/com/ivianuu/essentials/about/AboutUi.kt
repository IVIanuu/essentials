/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.about

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.donation.Donation
import com.ivianuu.essentials.donation.DonationKey
import com.ivianuu.essentials.license.ui.LicenseKey
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.rate.domain.RateOnPlayUseCase
import com.ivianuu.essentials.rate.ui.FeedbackMailKey
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.web.ui.WebKey
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.InjektCoroutineScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

object AboutKey : Key<Unit>

@Provide val aboutUi: ModelKeyUi<AboutKey, AboutModel> = {
  Scaffold(topBar = { TopAppBar(title = { Text(R.string.es_about_title) }) }) {
    LazyColumn(contentPadding = localVerticalInsetsPadding()) {
      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_info) },
          title = { Text(R.string.es_about_version) },
          subtitle = { Text(model.version) }
        )
      }

      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_star) },
          title = { Text(R.string.es_about_rate) },
          subtitle = { Text(R.string.es_about_rate_desc) },
          onClick = model.rate
        )
      }

      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_favorite) },
          title = { Text(R.string.es_about_donate) },
          onClick = model.donate
        )
      }

      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_google_play) },
          title = { Text(R.string.es_about_more_apps) },
          subtitle = { Text(R.string.es_about_more_apps_desc) },
          onClick = model.openMoreApps
        )
      }

      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_reddit) },
          title = { Text(R.string.es_about_reddit) },
          subtitle = { Text(R.string.es_about_reddit_desc) },
          onClick = model.openRedditPage
        )
      }

      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_github) },
          title = { Text(R.string.es_about_github) },
          subtitle = { Text(R.string.es_about_github_desc) },
          onClick = model.openGithubPage
        )
      }

      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_twitter) },
          title = { Text(R.string.es_about_twitter) },
          subtitle = { Text(R.string.es_about_twitter_desc) },
          onClick = model.openTwitterPage
        )
      }

      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_email) },
          title = { Text(R.string.es_about_feedback) },
          subtitle = { Text(model.email) },
          onClick = model.sendMail
        )
      }

      item {
        ListItem(
          leading = { Icon(R.drawable.es_ic_assignment) },
          title = { Text(R.string.es_licenses_title) },
          onClick = model.openLicenses
        )
      }

      if (model.privacyPolicyUrl != null) {
        item {
          ListItem(
            leading = { Icon(R.drawable.es_ic_policy) },
            title = { Text(R.string.es_about_privacy_policy) },
            onClick = model.openPrivacyPolicy
          )
        }
      }
    }
  }
}

@Optics data class AboutModel(
  val version: String = "",
  val email: String = "ivianuu@gmail.com",
  val privacyPolicyUrl: PrivacyPolicyUrl? = null,
  val showDonate: Boolean = false,
  val donate: () -> Unit = {},
  val rate: () -> Unit = {},
  val openLicenses: () -> Unit = {},
  val openMoreApps: () -> Unit = {},
  val openRedditPage: () -> Unit = {},
  val openGithubPage: () -> Unit = {},
  val openTwitterPage: () -> Unit = {},
  val openPrivacyPolicy: () -> Unit = {},
  val sendMail: () -> Unit = {}
) {
  companion object {
    @Provide fun initial(
      buildInfo: BuildInfo,
      privacyPolicyUrl: PrivacyPolicyUrl? = null,
      donations: (() -> Set<Donation>)? = null,
    ): @Initial AboutModel = AboutModel(
      version = buildInfo.versionName,
      privacyPolicyUrl = privacyPolicyUrl,
      showDonate = donations != null
    )
  }
}

@Provide fun aboutModel(
  initial: @Initial AboutModel,
  navigator: Navigator,
  rateOnPlayUseCase: RateOnPlayUseCase,
  scope: InjektCoroutineScope<KeyUiScope>,
  rp: ResourceProvider
): @Scoped<KeyUiScope> StateFlow<AboutModel> = scope.state(initial) {
  action(AboutModel.donate()) { navigator.push(DonationKey) }

  action(AboutModel.openLicenses()) { navigator.push(LicenseKey) }

  action(AboutModel.rate()) { rateOnPlayUseCase() }

  action(AboutModel.openMoreApps()) {
    navigator.push(UrlKey("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
  }

  action(AboutModel.openRedditPage()) {
    navigator.push(UrlKey("https://www.reddit.com/r/manuelwrageapps"))
  }

  action(AboutModel.openGithubPage()) {
    navigator.push(UrlKey("https://github.com/IVIanuu"))
  }

  action(AboutModel.openTwitterPage()) {
    navigator.push(UrlKey("https://twitter.com/IVIanuu"))
  }

  action(AboutModel.openPrivacyPolicy()) {
    navigator.push(
      WebKey(
        loadResource(R.string.es_about_privacy_policy),
        state.first().privacyPolicyUrl!!
      )
    )
  }

  action(AboutModel.sendMail()) { navigator.push(FeedbackMailKey) }
}

typealias PrivacyPolicyUrl = String

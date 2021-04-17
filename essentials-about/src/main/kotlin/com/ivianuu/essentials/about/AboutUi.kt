/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.foundation.lazy.*
import androidx.compose.material.Text
import androidx.compose.ui.res.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

class AboutKey : Key<Nothing>

@Given
val aboutUi: ModelKeyUi<AboutKey, AboutModel> = {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.about_title)) }) }) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_rate, )) },
                    subtitle = { Text(stringResource(R.string.about_rate_desc)) },
                    onClick = model.rate
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_more_apps)) },
                    subtitle = { Text(stringResource(R.string.about_more_apps_desc)) },
                    onClick = model.openMoreApps
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_reddit)) },
                    subtitle = { Text(stringResource(R.string.about_reddit_desc)) },
                    onClick = model.openRedditPage
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_github)) },
                    subtitle = { Text(stringResource(R.string.about_github_desc)) },
                    onClick = model.openGithubPage
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_twitter)) },
                    subtitle = { Text(stringResource(R.string.about_twitter_desc)) },
                    onClick = model.openTwitterPage
                )
            }

            if (model.privacyPolicyUrl != null) {
                item {
                    ListItem(
                        title = { Text(stringResource(R.string.about_privacy_policy)) },
                        onClick = model.openPrivacyPolicy
                    )
                }
            }
        }
    }
}

@Optics
data class AboutModel(
    val privacyPolicyUrl: PrivacyPolicyUrl? = null,
    val rate: () -> Unit = {},
    val openMoreApps: () -> Unit = {},
    val openRedditPage: () -> Unit = {},
    val openGithubPage: () -> Unit = {},
    val openTwitterPage: () -> Unit = {},
    val openPrivacyPolicy: () -> Unit = {}
) {
    companion object {
        @Given
        fun initial(
            @Given privacyPolicyUrl: PrivacyPolicyUrl? = null
        ): @Initial AboutModel = AboutModel(privacyPolicyUrl = privacyPolicyUrl)
    }
}

@Given
fun aboutModel(
    @Given buildInfo: BuildInfo,
    @Given initial: @Initial AboutModel,
    @Given navigator: Navigator,
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>
): @Scoped<KeyUiGivenScope> StateFlow<AboutModel> = scope.state(initial) {
    action(AboutModel.rate()) {
        navigator.push(
            UrlKey("https://play.google.com/store/apps/details?id=${buildInfo.packageName}")
        )
    }
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
        navigator.push(UrlKey(state.first().privacyPolicyUrl!!))
    }
}

typealias PrivacyPolicyUrl = String

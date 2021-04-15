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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.StateBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.StateKeyUi
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.first

class AboutKey : Key<Nothing>

@Given
val aboutUi: StateKeyUi<AboutKey, AboutState> = {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.about_title)) }) }) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_rate, )) },
                    subtitle = { Text(stringResource(R.string.about_rate_desc)) },
                    onClick = state.rate
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_more_apps)) },
                    subtitle = { Text(stringResource(R.string.about_more_apps_desc)) },
                    onClick = state.openMoreApps
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_reddit)) },
                    subtitle = { Text(stringResource(R.string.about_reddit_desc)) },
                    onClick = state.openRedditPage
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_github)) },
                    subtitle = { Text(stringResource(R.string.about_github_desc)) },
                    onClick = state.openGithubPage
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_twitter)) },
                    subtitle = { Text(stringResource(R.string.about_twitter_desc)) },
                    onClick = state.openTwitterPage
                )
            }

            if (state.privacyPolicyUrl != null) {
                item {
                    ListItem(
                        title = { Text(stringResource(R.string.about_privacy_policy)) },
                        onClick = state.openPrivacyPolicy
                    )
                }
            }
        }
    }
}

@Optics
data class AboutState(
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
        ): @Initial AboutState = AboutState(privacyPolicyUrl = privacyPolicyUrl)
    }
}

@Given
fun aboutState(
    @Given buildInfo: BuildInfo,
    @Given navigator: Navigator
): StateBuilder<KeyUiGivenScope, AboutState> = {
    action(AboutState.rate()) {
        navigator.push(
            UrlKey("https://play.google.com/store/apps/details?id=${buildInfo.packageName}")
        )
    }
    action(AboutState.openMoreApps()) {
        navigator.push(UrlKey("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
    }
    action(AboutState.openRedditPage()) {
        navigator.push(UrlKey("https://www.reddit.com/r/manuelwrageapps"))
    }
    action(AboutState.openGithubPage()) {
        navigator.push(UrlKey("https://github.com/IVIanuu"))
    }
    action(AboutState.openTwitterPage()) {
        navigator.push(UrlKey("https://twitter.com/IVIanuu"))
    }
    action(AboutState.openPrivacyPolicy()) {
        navigator.push(UrlKey(state.first().privacyPolicyUrl!!))
    }
}

typealias PrivacyPolicyUrl = String

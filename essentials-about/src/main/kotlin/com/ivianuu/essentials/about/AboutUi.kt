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
import com.ivianuu.essentials.about.AboutAction.*
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.effectOn
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.first

class AboutKey : Key<Nothing>

@Given
val aboutUi: StoreKeyUi<AboutKey, AboutState, AboutAction> = {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.about_title)) }) }) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_rate, )) },
                    subtitle = { Text(stringResource(R.string.about_rate_desc)) },
                    onClick = { tryEmit(Rate) }
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_more_apps)) },
                    subtitle = { Text(stringResource(R.string.about_more_apps_desc)) },
                    onClick = { tryEmit(OpenMoreApps) }
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_reddit)) },
                    subtitle = { Text(stringResource(R.string.about_reddit_desc)) },
                    onClick = { tryEmit(OpenRedditPage) }
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_github)) },
                    subtitle = { Text(stringResource(R.string.about_github_desc)) },
                    onClick = { tryEmit(OpenGithubPage) }
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_twitter)) },
                    subtitle = { Text(stringResource(R.string.about_twitter_desc)) },
                    onClick = { tryEmit(OpenTwitterPage) }
                )
            }

            if (state.privacyPolicyUrl != null) {
                item {
                    ListItem(
                        title = { Text(stringResource(R.string.about_privacy_policy)) },
                        onClick = { tryEmit(OpenPrivacyPolicy) }
                    )
                }
            }
        }
    }
}

data class AboutState(val privacyPolicyUrl: PrivacyPolicyUrl? = null) {
    companion object {
        @Given
        fun initial(
            @Given privacyPolicyUrl: PrivacyPolicyUrl? = null
        ): @Initial AboutState = AboutState(privacyPolicyUrl = privacyPolicyUrl)
    }
}

sealed class AboutAction {
    object Rate : AboutAction()
    object OpenMoreApps : AboutAction()
    object OpenRedditPage : AboutAction()
    object OpenGithubPage : AboutAction()
    object OpenTwitterPage : AboutAction()
    object OpenPrivacyPolicy : AboutAction()
}

@Given
fun aboutStore(
    @Given buildInfo: BuildInfo,
    @Given navigator: Navigator
): StoreBuilder<KeyUiGivenScope, AboutState, AboutAction> = {
    effectOn<Rate> {
        navigator.push(
            UrlKey("https://play.google.com/store/apps/details?id=${buildInfo.packageName}")
        )
    }
    effectOn<OpenMoreApps> {
        navigator.push(UrlKey("https://play.google.com/store/apps/developer?id=Manuel+Wrage"))
    }
    effectOn<OpenRedditPage> {
        navigator.push(UrlKey("https://www.reddit.com/r/manuelwrageapps"))
    }
    effectOn<OpenTwitterPage> {
        navigator.push(UrlKey("https://twitter.com/IVIanuu"))
    }
    effectOn<OpenPrivacyPolicy> {
        navigator.push(UrlKey(state.first().privacyPolicyUrl!!))
    }
}

typealias PrivacyPolicyUrl = String

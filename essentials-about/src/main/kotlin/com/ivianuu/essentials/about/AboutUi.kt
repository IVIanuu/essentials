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
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.ui.navigation.ViewModelKeyUi
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

class AboutKey : Key<Nothing>

@Given
val aboutUi: ViewModelKeyUi<AboutKey, AboutViewModel, AboutState> = { viewModel, state ->
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.about_title)) }) }) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_rate, )) },
                    subtitle = { Text(stringResource(R.string.about_rate_desc)) },
                    onClick = { viewModel.rate() }
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_more_apps)) },
                    subtitle = { Text(stringResource(R.string.about_more_apps_desc)) },
                    onClick = { viewModel.openMoreApps() }
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_reddit)) },
                    subtitle = { Text(stringResource(R.string.about_reddit_desc)) },
                    onClick = { viewModel.openRedditPage() }
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_github)) },
                    subtitle = { Text(stringResource(R.string.about_github_desc)) },
                    onClick = { viewModel.openGithubPage() }
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_twitter)) },
                    subtitle = { Text(stringResource(R.string.about_twitter_desc)) },
                    onClick = { viewModel.openTwitterPage() }
                )
            }

            if (state.privacyPolicyUrl != null) {
                item {
                    ListItem(
                        title = { Text(stringResource(R.string.about_privacy_policy)) },
                        onClick = { viewModel.openPrivacyPolicy()  }
                    )
                }
            }
        }
    }
}

data class AboutState(val privacyPolicyUrl: PrivacyPolicyUrl? = null) : State() {
    companion object {
        @Given
        fun initial(
            @Given privacyPolicyUrl: PrivacyPolicyUrl? = null
        ): @Initial AboutState = AboutState(privacyPolicyUrl = privacyPolicyUrl)
    }
}

@Scoped<KeyUiGivenScope>
@Given
class AboutViewModel(
    @Given private val buildInfo: BuildInfo,
    @Given private val navigator: Collector<NavigationAction>,
    @Given private val store: ScopeStateStore<KeyUiGivenScope, AboutState>
) : StateFlow<AboutState> by store {
    fun rate() = store.effect {
        navigator(
            Push(
                UrlKey("https://play.google.com/store/apps/details?id=${buildInfo.packageName}")
            )
        )
    }
    fun openMoreApps() = store.effect {
        navigator(Push(UrlKey("https://play.google.com/store/apps/developer?id=Manuel+Wrage")))
    }
    fun openRedditPage() = store.effect {
        navigator(Push(UrlKey("https://www.reddit.com/r/manuelwrageapps")))
    }
    fun openGithubPage() = store.effect {
        navigator(Push(UrlKey("https://github.com/IVIanuu")))
    }
    fun openTwitterPage() = store.effect {
        navigator(Push(UrlKey("https://twitter.com/IVIanuu")))
    }
    fun openPrivacyPolicy() = store.effect {
        navigator(Push(UrlKey(store.first().privacyPolicyUrl!!)))
    }
}

typealias PrivacyPolicyUrl = String

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.about.AboutAction.OpenGithubPage
import com.ivianuu.essentials.about.AboutAction.OpenMoreApps
import com.ivianuu.essentials.about.AboutAction.OpenPrivacyPolicy
import com.ivianuu.essentials.about.AboutAction.OpenRedditPage
import com.ivianuu.essentials.about.AboutAction.OpenTwitterPage
import com.ivianuu.essentials.about.AboutAction.Rate
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.core.localVerticalInsets
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.ComponentCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AboutKey : Key<Nothing>

@Module
val aboutKeyModule = KeyModule<AboutKey>()

@Given
fun aboutUi(
    @Given stateFlow: StateFlow<AboutState>,
    @Given dispatch: Collector<AboutAction>
): KeyUi<AboutKey> = {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.about_title)) }) }) {
        val state by stateFlow.collectAsState()
        LazyColumn(contentPadding = localVerticalInsets()) {
            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_rate,)) },
                    subtitle = { Text(stringResource(R.string.about_rate_desc)) },
                    onClick = { dispatch(Rate) }
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_more_apps)) },
                    subtitle = { Text(stringResource(R.string.about_more_apps_desc)) },
                    onClick = { dispatch(OpenMoreApps) }
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_reddit)) },
                    subtitle = { Text(stringResource(R.string.about_reddit_desc)) },
                    onClick = { dispatch(OpenRedditPage) }
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_github)) },
                    subtitle = { Text(stringResource(R.string.about_github_desc)) },
                    onClick = { dispatch(OpenGithubPage) }
                )
            }

            item {
                ListItem(
                    title = { Text(stringResource(R.string.about_twitter)) },
                    subtitle = { Text(stringResource(R.string.about_twitter_desc)) },
                    onClick = { dispatch(OpenTwitterPage) }
                )
            }

            if (state.privacyPolicyUrl != null) {
                item {
                    ListItem(
                        title = { Text(stringResource(R.string.about_privacy_policy)) },
                        onClick = { dispatch(OpenPrivacyPolicy) }
                    )
                }
            }
        }
    }
}

data class AboutState(
    val privacyPolicyUrl: PrivacyPolicyUrl? = null
)

sealed class AboutAction {
    object Rate : AboutAction()
    object OpenMoreApps : AboutAction()
    object OpenRedditPage : AboutAction()
    object OpenGithubPage : AboutAction()
    object OpenTwitterPage : AboutAction()
    object OpenPrivacyPolicy : AboutAction()
}

@Scoped<KeyUiComponent>
@Given
fun aboutState(
    @Given scope: ComponentCoroutineScope<KeyUiComponent>,
    @Given initial: @Initial AboutState = AboutState(),
    @Given actions: Flow<AboutAction>,
    @Given buildInfo: BuildInfo,
    @Given navigator: Collector<NavigationAction>,
    @Given privacyPolicyUrl: PrivacyPolicyUrl? = null
): StateFlow<AboutState> = scope.state(initial.copy(privacyPolicyUrl = privacyPolicyUrl)) {
    actions
        .filterIsInstance<Rate>()
        .onEach {
            navigator(
                Push(
                    UrlKey(
                        "https://play.google.com/store/apps/details?id=${buildInfo.packageName}"
                    )
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenMoreApps>()
        .onEach {
            navigator(
                Push(
                    UrlKey(
                        "https://play.google.com/store/apps/developer?id=Manuel+Wrage"
                    )
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenRedditPage>()
        .onEach {
            navigator(
                Push(
                    UrlKey(
                        "https://www.reddit.com/r/manuelwrageapps"
                    )
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenGithubPage>()
        .onEach {
            navigator(
                Push(
                    UrlKey(
                        "https://github.com/IVIanuu"
                    )
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenTwitterPage>()
        .onEach {
            navigator(
                Push(
                    UrlKey(
                        "https://twitter.com/IVIanuu"
                    )
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenPrivacyPolicy>()
        .onEach {
            navigator(
                Push(
                    UrlKey(
                        state.first().privacyPolicyUrl!!
                    )
                )
            )
        }
        .launchIn(this)
}

@Scoped<KeyUiComponent>
@Given
val aboutActions get() = EventFlow<AboutAction>()

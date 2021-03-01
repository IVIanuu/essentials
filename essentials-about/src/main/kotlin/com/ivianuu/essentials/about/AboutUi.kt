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
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.core.localVerticalInsets
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module

class AboutKey : Key<Nothing>

@Module
val aboutKeyModule = KeyModule<AboutKey>()

@Given
fun aboutUi(
    @Given aboutSection: AboutSection,
    @Given buildInfo: BuildInfo,
    @Given privacyPolicyUrl: PrivacyPolicyUrl? = null
): KeyUi<AboutKey> = {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.about_title)) }) }) {
        LazyColumn(contentPadding = localVerticalInsets()) {
            item {
                aboutSection(
                    buildInfo.packageName,
                    false,
                    privacyPolicyUrl
                )
            }
        }
    }
}

typealias AboutSection = @Composable (String, Boolean, PrivacyPolicyUrl?) -> Unit

@Given
fun aboutSection(
    @Given navigator: DispatchAction<NavigationAction>
): AboutSection = { packageName, showHeader, privacyPolicyUrl ->
    if (showHeader) {
        Subheader {
            Text(stringResource(R.string.about_title))
        }
    }

    AboutItem(
        titleRes = R.string.about_rate,
        descRes = R.string.about_rate_desc,
        url = { "https://play.google.com/store/apps/details?id=$packageName" },
        navigator = navigator
    )

    AboutItem(
        titleRes = R.string.about_more_apps,
        descRes = R.string.about_more_apps_desc,
        url = { "https://play.google.com/store/apps/developer?id=Manuel+Wrage" },
        navigator = navigator
    )

    AboutItem(
        titleRes = R.string.about_reddit,
        descRes = R.string.about_reddit_desc,
        url = { "https://www.reddit.com/r/manuelwrageapps" },
        navigator = navigator
    )

    AboutItem(
        titleRes = R.string.about_github,
        descRes = R.string.about_github_desc,
        url = { "https://github.com/IVIanuu" },
        navigator = navigator
    )

    AboutItem(
        titleRes = R.string.about_twitter,
        descRes = R.string.about_twitter_desc,
        url = { "https://twitter.com/IVIanuu" },
        navigator = navigator
    )

    if (privacyPolicyUrl != null) {
        AboutItem(
            titleRes = R.string.about_privacy_policy,
            url = { privacyPolicyUrl },
            navigator = navigator
        )
    }
}

@Composable
private fun AboutItem(
    titleRes: Int,
    descRes: Int? = null,
    url: () -> String,
    navigator: DispatchAction<NavigationAction>,
) {
    ListItem(
        title = { Text(stringResource(titleRes)) },
        subtitle = descRes?.let {
            {
                Text(stringResource(it))
            }
        },
        onClick = { navigator(Push(UrlKey(url()))) }
    )
}

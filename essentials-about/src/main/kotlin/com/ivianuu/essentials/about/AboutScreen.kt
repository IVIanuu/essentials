/*
 * Copyright 2019 Manuel Wrage
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

import androidx.compose.Composable
import com.ivianuu.essentials.ui.compose.common.openUrlOnClick
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollableList
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.staticComposableWithKey
import com.ivianuu.essentials.ui.compose.es.composeControllerRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.compose.material.Subheader
import com.ivianuu.essentials.ui.compose.resources.stringResource
import com.ivianuu.essentials.ui.navigation.director.defaultControllerRouteOptionsOrNull
import com.ivianuu.essentials.util.BuildInfo

fun aboutRoute(
    hasDebugPackageName: Boolean = true,
    privacyPolicyUrl: String? = null
) = composeControllerRoute(
    options = defaultControllerRouteOptionsOrNull()
) {
    Scaffold(
        topAppBar = { EsTopAppBar(stringResource(R.string.about_title)) },
        body = {
            ScrollableList {
                AboutSection(
                    hasDebugPackageName = hasDebugPackageName,
                    showHeader = false,
                    privacyPolicyUrl = privacyPolicyUrl
                )
            }
        }
    )
}

@Composable
fun AboutSection(
    buildInfo: BuildInfo = inject(),
    showHeader: Boolean = false,
    hasDebugPackageName: Boolean = buildInfo.isDebug,
    privacyPolicyUrl: String? = null
) = composable {
    if (showHeader) {
        Subheader(stringResource(R.string.about_title))
    }

    AboutItem(
        titleRes = R.string.about_rate,
        descRes = R.string.about_rate_desc,
        url = {
            val packageName = if (hasDebugPackageName && buildInfo.isDebug) {
                buildInfo.packageName.removeSuffix(".debug")
            } else {
                buildInfo.packageName
            }

            return@AboutItem "https://play.google.com/store/apps/details?id=$packageName"
        }
    )

    AboutItem(
        titleRes = R.string.about_more_apps,
        descRes = R.string.about_more_apps_desc,
        url = { "https://play.google.com/store/apps/developer?id=Manuel+Wrage" }
    )

    AboutItem(
        titleRes = R.string.about_reddit,
        descRes = R.string.about_reddit_desc,
        url = { "https://www.reddit.com/r/manuelwrageapps" }
    )

    AboutItem(
        titleRes = R.string.about_github,
        descRes = R.string.about_github_desc,
        url = { "https://github.com/IVIanuu" }
    )

    AboutItem(
        titleRes = R.string.about_twitter,
        descRes = R.string.about_twitter_desc,
        url = { "https://twitter.com/IVIanuu" }
    )

    if (privacyPolicyUrl != null) {
        AboutItem(
            titleRes = R.string.about_privacy_policy,
            url = { privacyPolicyUrl }
        )
    }
}

@Composable
fun AboutItem(
    titleRes: Int,
    descRes: Int? = null,
    url: () -> String
) = staticComposableWithKey(titleRes) {
    SimpleListItem(
        title = stringResource(titleRes),
        subtitle = descRes?.let { stringResource(it) },
        onClick = openUrlOnClick(url)
    )
}

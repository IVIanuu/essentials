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
import androidx.compose.unaryPlus
import androidx.ui.material.ListItem
import androidx.ui.res.stringResource
import com.ivianuu.essentials.ui.compose.common.openUrlOnClick
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Subheader
import com.ivianuu.essentials.ui.compose.prefs.PrefsScreen
import com.ivianuu.essentials.ui.navigation.director.defaultControllerRouteOptionsOrNull
import com.ivianuu.essentials.util.BuildInfo

fun aboutRoute(
    hasDebugPackageName: Boolean = true,
    privacyPolicyUrl: String? = null
) = composeControllerRoute(
    options = defaultControllerRouteOptionsOrNull()
) {
    PrefsScreen(
        appBar = { EsTopAppBar(+stringResource(R.string.about_title)) },
        prefs = {
            AboutSection(
                hasDebugPackageName = hasDebugPackageName,
                showHeader = false,
                privacyPolicyUrl = privacyPolicyUrl
            )
        }
    )
}

@Composable
fun AboutSection(
    buildInfo: BuildInfo = +inject<BuildInfo>(),
    showHeader: Boolean = false,
    hasDebugPackageName: Boolean = buildInfo.isDebug,
    privacyPolicyUrl: String? = null
) = composable("AboutSection") {
    if (showHeader) {
        Subheader(+stringResource(R.string.about_title))
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
) = composable(titleRes + (descRes ?: 0)) {
    ListItem(
        text = +stringResource(titleRes),
        secondaryText = descRes?.let { +stringResource(it) },
        onClick = +openUrlOnClick(url)
    )
}
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
import androidx.ui.foundation.VerticalScroller
import com.ivianuu.essentials.ui.common.RetainedScrollerPosition
import com.ivianuu.essentials.ui.common.openUrlOnClick
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.Transient

@Transient
class AboutPage(
    private val buildInfo: BuildInfo,
    private val privacyPolicyUrl: @PrivacyPolicyUrl String? = null
) {
    @Composable
    operator fun invoke() {
        Scaffold(
            topAppBar = {
                TopAppBar(title = { Text(R.string.about_title) })
            },
            body = {
                VerticalScroller(scrollerPosition = RetainedScrollerPosition()) {
                    AboutSection(
                        showHeader = false,
                        packageName = buildInfo.packageName,
                        privacyPolicyUrl = privacyPolicyUrl
                    )
                }
            }
        )
    }
}

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class PrivacyPolicyUrl

@Composable
fun AboutSection(
    showHeader: Boolean = false,
    packageName: String,
    privacyPolicyUrl: String? = null
) {
    if (showHeader) {
        Subheader {
            Text(R.string.about_title)
        }
    }

    AboutItem(
        titleRes = R.string.about_rate,
        descRes = R.string.about_rate_desc,
        url = { "https://play.google.com/store/apps/details?id=$packageName" }
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
private fun AboutItem(
    titleRes: Int,
    descRes: Int? = null,
    url: () -> String
) {
    ListItem(
        title = { Text(titleRes) },
        subtitle = descRes?.let {
            {
                Text(it)
            }
        },
        onClick = openUrlOnClick(url)
    )
}

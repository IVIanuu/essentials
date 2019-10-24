package com.ivianuu.essentials.about

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.material.ListItem
import androidx.ui.res.stringResource
import com.ivianuu.essentials.ui.common.urlRoute
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Subheader
import com.ivianuu.essentials.ui.compose.prefs.PrefsScreen
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
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
        route = {
            val packageName = if (hasDebugPackageName && buildInfo.isDebug) {
                buildInfo.packageName.removeSuffix(".debug")
            } else {
                buildInfo.packageName
            }

            urlRoute("https://play.google.com/store/apps/details?id=$packageName")
        }
    )

    AboutItem(
        titleRes = R.string.about_more_apps,
        descRes = R.string.about_more_apps_desc,
        route = { urlRoute("https://play.google.com/store/apps/developer?id=Manuel+Wrage") }
    )

    AboutItem(
        titleRes = R.string.about_reddit,
        descRes = R.string.about_reddit_desc,
        route = { urlRoute("https://www.reddit.com/r/manuelwrageapps\"") }
    )

    AboutItem(
        titleRes = R.string.about_github,
        descRes = R.string.about_github_desc,
        route = { urlRoute("https://github.com/IVIanuu") }
    )

    AboutItem(
        titleRes = R.string.about_twitter,
        descRes = R.string.about_twitter_desc,
        route = { urlRoute("https://twitter.com/IVIanuu") }
    )

    if (privacyPolicyUrl != null) {
        AboutItem(
            titleRes = R.string.about_privacy_policy,
            route = { urlRoute(privacyPolicyUrl) }
        )
    }
}

@Composable
fun AboutItem(
    titleRes: Int,
    descRes: Int? = null,
    route: () -> Route
) = composable(titleRes + (descRes ?: 0)) {
    val navigator = +inject<Navigator>()
    ListItem(
        text = +stringResource(titleRes),
        secondaryText = descRes?.let { +stringResource(it) },
        onClick = { navigator.push(route()) }
    )
}
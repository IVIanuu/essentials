package com.ivianuu.essentials.about

import com.airbnb.epoxy.EpoxyController
import com.ivianuu.epoxyprefs.CategoryPreference
import com.ivianuu.epoxyprefs.Preference
import com.ivianuu.essentials.ui.common.urlRoute
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.navigation.director.defaultControllerRouteOptionsOrNull
import com.ivianuu.essentials.ui.prefs.PrefsController
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf

fun aboutRoute(
    hasDebugPackageName: Boolean = true,
    privacyPolicyUrl: String? = null
) = controllerRoute<AboutController>(
    options = defaultControllerRouteOptionsOrNull()
) {
    parametersOf(hasDebugPackageName, privacyPolicyUrl)
}

@Inject
internal class AboutController(
    private val buildInfo: BuildInfo,
    @Param private val hasDebugPackageName: Boolean,
    @Param private val privacyPolicyUrl: String?
) : PrefsController() {

    override val toolbarTitleRes: Int?
        get() = R.string.about_title

    override fun epoxyController() = epoxyController {
        AboutSection(
            buildInfo = buildInfo,
            navigator = navigator,
            hasDebugPackageName = hasDebugPackageName,
            showHeader = false,
            privacyPolicyUrl = privacyPolicyUrl
        )
    }

}

fun EpoxyController.AboutSection(
    buildInfo: BuildInfo,
    navigator: Navigator,
    showHeader: Boolean = false,
    hasDebugPackageName: Boolean = buildInfo.isDebug,
    privacyPolicyUrl: String? = null
) {
    if (showHeader) {
        CategoryPreference {
            key("about_header")
            titleRes(R.string.about_title)
        }
    }

    Preference {
        key("about_rate")
        titleRes(R.string.about_rate)
        summaryRes(R.string.about_rate_summary)
        onClick {
            val packageName = if (hasDebugPackageName && buildInfo.isDebug) {
                buildInfo.packageName.removeSuffix(".debug")
            } else {
                buildInfo.packageName
            }
            navigator.push(
                urlRoute("https://play.google.com/store/apps/details?id=$packageName")
            )
            return@onClick true
        }
    }

    Preference {
        key("about_more_apps")
        titleRes(R.string.about_more_apps)
        summaryRes(R.string.about_more_apps_summary)
        onClick {
            navigator.push(
                urlRoute("https://play.google.com/store/apps/developer?id=Manuel+Wrage")
            )
            return@onClick true
        }
    }

    Preference {
        key("about_reddit")
        titleRes(R.string.about_reddit)
        summaryRes(R.string.about_reddit_summary)
        onClick {
            navigator.push(
                urlRoute("https://www.reddit.com/r/manuelwrageapps")
            )
            return@onClick true
        }
    }

    Preference {
        key("about_github")
        titleRes(R.string.about_github)
        summaryRes(R.string.about_github_summary)
        onClick {
            navigator.push(
                urlRoute("https://github.com/IVIanuu")
            )
            return@onClick true
        }
    }

    Preference {
        key("about_twitter")
        titleRes(R.string.about_twitter)
        summaryRes(R.string.about_twitter_summary)
        onClick {
            navigator.push(
                urlRoute("https://twitter.com/IVIanuu")
            )
            return@onClick true
        }
    }

    if (privacyPolicyUrl != null) {
        Preference {
            key("privacy_policy")
            titleRes(R.string.about_privacy_policy)
            onClick {
                navigator.push(
                    urlRoute(privacyPolicyUrl)
                )
                return@onClick true
            }
        }
    }
}
package com.ivianuu.essentials.sample.ui

import android.view.View
import com.ivianuu.compose.ViewByLayoutRes
import com.ivianuu.compose.ambient
import com.ivianuu.compose.common.NavigatorAmbient
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.init
import com.ivianuu.essentials.hidenavbar.NavBarConfig
import com.ivianuu.essentials.hidenavbar.NavBarController
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.securesettings.SecureSettingsHelper
import com.ivianuu.essentials.securesettings.SecureSettingsRoute
import com.ivianuu.essentials.ui.changehandler.OpenCloseChangeHandler
import com.ivianuu.essentials.ui.compose.holder
import com.ivianuu.essentials.ui.compose.injekt.inject
import kotlinx.android.synthetic.main.nav_bar.view.*

fun NavBarRoute() = Route(handler = OpenCloseChangeHandler()) {
    ViewByLayoutRes<View>(layoutRes = R.layout.nav_bar) {
        val (navBarHidden, setNavBarHidden) = holder { false }
        val navigator = ambient(NavigatorAmbient)
        val navBarController = inject<NavBarController>()
        val secureSettingsHelper = inject<SecureSettingsHelper>()
        init {
            toggle_nav_bar.setOnClickListener {
                if (secureSettingsHelper.canWriteSecureSettings()) {
                    val newState = !navBarHidden
                    setNavBarHidden(newState)
                    navBarController.setNavBarConfig(NavBarConfig(hidden = newState))
                } else {
                    navigator.push(
                        SecureSettingsRoute(showHideNavBarHint = true)
                            .withHandlers(OpenCloseChangeHandler())
                    )
                }
            }
        }
    }
}
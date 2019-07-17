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

package com.ivianuu.essentials.securesettings

import androidx.lifecycle.lifecycleScope
import com.ivianuu.epoxyprefs.Preference
import com.ivianuu.essentials.ui.changehandler.verticalFade
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.navigation.director.copy
import com.ivianuu.essentials.ui.navigation.director.defaultControllerRouteOptionsOrElse
import com.ivianuu.essentials.ui.prefs.PrefsController
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Asks the user for the secure settings permission
 */
fun secureSettingsRoute(showHideNavBarHint: Boolean = false) =
    controllerRoute<SecureSettingsController> {
        parametersOf(showHideNavBarHint)
    }

@Inject
class SecureSettingsController(
    @Param private val showHideNavBarHint: Boolean,
    private val secureSettingsHelper: SecureSettingsHelper,
    private val toaster: Toaster
) : PrefsController() {

    override val toolbarTitleRes: Int
        get() = R.string.es_title_secure_settings

    override fun onCreate() {
        super.onCreate()

        lifecycleScope.launch {
            while (true) {
                if (secureSettingsHelper.canWriteSecureSettings()) {
                    handlePermissionResult(true)
                    break
                }
                delay(500)
            }
        }
    }

    override fun epoxyController() = epoxyController {
        Preference {
            key("secure_settings_header")
            summaryRes(
                if (showHideNavBarHint) {
                    R.string.es_pref_secure_settings_header_hide_nav_bar_summary
                } else {
                    R.string.es_pref_secure_settings_header_summary
                }
            )
        }

        Preference {
            key("use_pc")
            titleRes(R.string.es_pref_use_pc)
            summaryRes(R.string.es_pref_use_pc_summary)
            navigateOnClick {
                secureSettingsInstructionsRoute.copy(
                    options = defaultControllerRouteOptionsOrElse {
                        ControllerRoute.Options().verticalFade()
                    }
                )
            }
        }

        Preference {
            key("use_root")
            titleRes(R.string.es_pref_use_root)
            summaryRes(R.string.es_pref_use_root_summary)
            onClick {
                lifecycleScope.launch {
                    if (secureSettingsHelper.grantWriteSecureSettings()) {
                        handlePermissionResult(true)
                    } else {
                        toaster.toast(R.string.es_secure_settings_no_root)
                    }
                }

                return@onClick true
            }
        }
    }

    private fun handlePermissionResult(success: Boolean) {
        if (success) {
            toaster.toast(R.string.es_secure_settings_permission_granted)
            navigator.pop(success)
        } else {
            toaster.toast(R.string.es_secure_settings_permission_denied)
        }
    }
}
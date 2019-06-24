/*
 * Copyright 2018 Manuel Wrage
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

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.ivianuu.epoxyprefs.Preference
import com.ivianuu.essentials.ui.changehandler.verticalFade
import com.ivianuu.essentials.ui.prefs.PrefsController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.defaultNavOptionsOrElse
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.ext.ResultKey
import com.ivianuu.essentials.util.ext.goBackWithResult
import com.ivianuu.injekt.inject
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch

@Parcelize
class SecureSettingsKey(
    val showHideNavBarHint: Boolean = false,
    override var resultCode: Int
) : ControllerKey(::SecureSettingsController), ResultKey<Boolean>

/**
 * Asks the user for the secure settings permission
 */
class SecureSettingsController : PrefsController() {

    override val toolbarTitleRes: Int
        get() = R.string.es_title_secure_settings

    private val key by inject<SecureSettingsKey>()
    private val secureSettingsHelper by inject<SecureSettingsHelper>()
    private val toaster by inject<Toaster>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (secureSettingsHelper.canWriteSecureSettings()) {
            handlePermissionResult(true)
        }
    }

    override fun epoxyController() = epoxyController {
        Preference {
            key("secure_settings_header")
            summaryRes(
                if (this@SecureSettingsController.key.showHideNavBarHint) {
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
            navigateOnClickWithOptions {
                SecureSettingsPcInstructionsKey to defaultNavOptionsOrElse {
                    NavOptions().verticalFade()
                }
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
            travelerRouter.goBackWithResult(key.resultCode, true)
        } else {
            toaster.toast(R.string.es_secure_settings_permission_denied)
        }
    }
}
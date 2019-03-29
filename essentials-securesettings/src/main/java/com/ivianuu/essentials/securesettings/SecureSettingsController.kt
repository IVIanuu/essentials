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

import android.view.View
import com.ivianuu.director.activity
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.ui.common.VerticalFadeChangeHandler
import com.ivianuu.essentials.ui.prefs.PrefsController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.handler
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.ext.coroutineScope
import com.ivianuu.essentials.util.ext.goBackWithResult
import com.ivianuu.essentials.util.ext.sendResult
import com.ivianuu.essentials.util.ext.toast
import com.ivianuu.injekt.inject
import com.ivianuu.list.common.modelController
import com.ivianuu.listprefs.key
import com.ivianuu.listprefs.onClick
import com.ivianuu.listprefs.summary
import com.ivianuu.listprefs.title
import com.ivianuu.traveler.navigate
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch

@Parcelize
class SecureSettingsKey(
    val resultCode: Int,
    val showHideNavBarHint: Boolean = false
) : ControllerKey(::SecureSettingsController)

/**
 * Asks the user for the secure settings permission
 */
class SecureSettingsController : PrefsController() {

    override val toolbarTitleRes: Int
        get() = R.string.es_title_secure_settings

    private val key by inject<SecureSettingsKey>()
    private val shell by inject<Shell>()

    override fun modelController() = modelController {
        preference {
            key("secure_settings_header")
            summary(
                if (this@SecureSettingsController.key.showHideNavBarHint) {
                    R.string.es_pref_summary_secure_settings_header_hide_nav_bar
                } else {
                    R.string.es_pref_summary_secure_settings_header
                }
            )
        }

        preference {
            key("use_pc")
            summary(R.string.es_pref_summary_use_pc)
            title(R.string.es_pref_title_use_pc)
            onClick {
                travelerRouter.navigate(
                    SecureSettingsPcInstructionsKey(),
                    NavOptions().handler(VerticalFadeChangeHandler())
                )
                return@onClick true
            }
        }

        preference {
            key("use_root")
            summary(R.string.es_pref_summary_use_root)
            title(R.string.es_pref_title_use_root)
            onClick {
                coroutineScope.launch {
                    try {
                        shell.run("pm grant ${activity.packageName} android.permission.WRITE_SECURE_SETTINGS")
                        handlePermissionResult(activity.canWriteSecureSettings())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toast(R.string.es_msg_secure_settings_no_root)
                    }
                }
                return@onClick true
            }
        }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        if (canWriteSecureSettings()) {
            handlePermissionResult(true)
        }
    }

    override fun handleBack(): Boolean {
        // todo should we send a result in this case?
        travelerRouter.sendResult(key.resultCode, false)
        return super.handleBack()
    }

    private fun handlePermissionResult(success: Boolean) {
        if (success) {
            toast(R.string.es_msg_secure_settings_permission_granted)
            travelerRouter.goBackWithResult(key.resultCode, true)
        } else {
            toast(R.string.es_msg_secure_settings_permission_denied)
        }
    }
}
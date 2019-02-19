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
import com.ivianuu.director.context
import com.ivianuu.epoxyktx.epoxyController
import com.ivianuu.epoxyprefs.preference
import com.ivianuu.epoxyprefs.summary
import com.ivianuu.epoxyprefs.title
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.ui.common.VerticalFadeChangeHandler
import com.ivianuu.essentials.ui.simple.SimpleController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.handler
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.ext.goBackWithResult
import com.ivianuu.essentials.util.ext.sendResult
import com.ivianuu.essentials.util.ext.toast
import com.ivianuu.injekt.inject
import com.ivianuu.traveler.navigate
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch

@Parcelize
class SecureSettingsKey(
    val resultCode: Int
) : ControllerKey(::SecureSettingsController)

/**
 * Asks the user for the secure settings permission
 */
class SecureSettingsController : SimpleController() {

    override val toolbarTitleRes: Int
        get() = R.string.es_screen_label_secure_settings

    private val key by inject<SecureSettingsKey>()
    private val shell by inject<Shell>()

    override fun epoxyController() = epoxyController {
        preference(context) {
            key("secure_settings_header")
            summary(R.string.es_pref_summary_secure_settings_header)
        }

        preference(context) {
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

        preference(context) {
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
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        if (canWriteSecureSettings()) {
            handlePermissionResult(true)
        }
    }

    override fun handleBack(): Boolean {
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
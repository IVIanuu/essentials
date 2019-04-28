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

import com.ivianuu.director.activity
import com.ivianuu.essentials.ui.prefs.PrefsController
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.string
import com.ivianuu.injekt.inject
import com.ivianuu.list.common.itemController
import com.ivianuu.listprefs.icon
import com.ivianuu.listprefs.onClickUrl
import com.ivianuu.listprefs.summary
import com.ivianuu.listprefs.title
import kotlinx.android.parcel.Parcelize

@Parcelize
class SecureSettingsPcInstructionsKey : ControllerKey(::SecureSettingsPcInstructionsController)

/**
 * Asks the user for the secure settings permission
 */
class SecureSettingsPcInstructionsController : PrefsController() {

    private val clipboardHelper by inject<ClipboardAccessor>()
    private val toaster by inject<Toaster>()

    override val toolbarTitleRes: Int
        get() = R.string.es_title_secure_settings_pc_instructions

    override fun itemController() = itemController {
        PreferenceItem {
            key = "secure_settings_header"
            summary(R.string.es_pref_summary_secure_settings_pc_instructions_header)
        }

        PreferenceItem {
            key = "secure_settings_step_1"
            title(R.string.es_pref_title_secure_settings_step_1)
            summary(R.string.es_pref_summary_secure_settings_step_1)
            clickable = false
        }

        PreferenceItem {
            key = "secure_settings_step_two"
            title(R.string.es_pref_title_secure_settings_step_2)
            summary(R.string.es_pref_summary_secure_settings_step_2)
            clickable = false
        }

        PreferenceItem {
            key = "secure_settings_step_3"
            title(R.string.es_pref_title_secure_settings_step_3)
        }

        PreferenceItem {
            key = "secure_settings_link_gadget_hacks"
            icon(R.drawable.es_ic_link)
            summary(R.string.es_pref_summary_secure_settings_link_gadget_hacks)
            onClickUrl { "https://youtu.be/CDuxcrrWLnY" }
        }

        PreferenceItem {
            key = "secure_settings_link_lifehacker"
            icon(R.drawable.es_ic_link)
            summary(R.string.es_pref_summary_secure_settings_link_lifehacker)
            onClickUrl { "https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378" }
        }

        PreferenceItem {
            key = "secure_settings_link_xda"
            icon(R.drawable.es_ic_link)
            summary(R.string.es_pref_summary_secure_settings_link_xda)
            onClickUrl { "https://www.xda-developers.com/install-adb-windows-macos-linux/" }
        }

        PreferenceItem {
            key = "secure_settings_step_4"
            title(R.string.es_pref_title_secure_settings_step_4)
            summary = string(R.string.es_pref_summary_secure_settings_step_4, activity.packageName)
            onClick {
                clipboardHelper.clipboardText =
                    "adb shell pm grant ${activity.packageName} android.permission.WRITE_SECURE_SETTINGS"

                toaster.toast(R.string.es_msg_secure_settings_copied_to_clipboard)
                return@onClick true
            }
        }
    }

}
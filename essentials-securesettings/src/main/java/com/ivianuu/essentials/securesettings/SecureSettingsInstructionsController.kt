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

import android.content.ClipboardManager
import com.ivianuu.epoxyktx.epoxyController
import com.ivianuu.epoxyprefs.icon
import com.ivianuu.epoxyprefs.onClickUrl
import com.ivianuu.epoxyprefs.summary
import com.ivianuu.epoxyprefs.title
import com.ivianuu.essentials.ui.prefs.PrefsController
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.ext.toast
import com.ivianuu.essentials.util.string
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@Parcelize
class SecureSettingsInstructionsKey : ControllerKey(SecureSettingsInstructionsController::class)

/**
 * Asks the user for the secure settings permission
 */
class SecureSettingsInstructionsController : PrefsController() {

    @Inject lateinit var clipboardManager: ClipboardManager

    override val toolbarTitleRes: Int
        get() = R.string.es_screen_label_secure_settings_instructions

    override fun epoxyController() = epoxyController {
        preference {
            key("secure_settings_header")
            summary(R.string.es_pref_summary_secure_settings_header)
        }

        preference {
            key("secure_settings_step_1")
            title(R.string.es_pref_title_secure_settings_step_1)
            summary(R.string.es_pref_summary_secure_settings_step_1)
            clickable(false)
        }

        preference {
            key("secure_settings_step_two")
            title(R.string.es_pref_title_secure_settings_step_2)
            summary(R.string.es_pref_summary_secure_settings_step_2)
            clickable(false)
        }

        preference {
            key("secure_settings_step_3")
            title(R.string.es_pref_title_secure_settings_step_3)
        }

        preference {
            key("secure_settings_link_gadget_hacks")
            icon(R.drawable.ic_link)
            summary(R.string.es_pref_summary_secure_settings_link_gadget_hacks)
            onClickUrl { "https://youtu.be/CDuxcrrWLnY" }
        }

        preference {
            key("secure_settings_link_lifehacker")
            icon(R.drawable.ic_link)
            summary(R.string.es_pref_summary_secure_settings_link_lifehacker)
            onClickUrl { "https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378" }
        }

        preference {
            key("secure_settings_link_xda")
            icon(R.drawable.ic_link)
            summary(R.string.es_pref_summary_secure_settings_link_xda)
            onClickUrl { "https://www.xda-developers.com/install-adb-windows-macos-linux/" }
        }

        preference {
            key("secure_settings_step_4")
            title(R.string.es_pref_title_secure_settings_step_4)
            summary(string(R.string.es_pref_summary_secure_settings_step_4, activity.packageName))
            onClick {
                clipboardManager.text =
                        "adb shell pm grant ${activity.packageName} android.permission.WRITE_SECURE_SETTINGS"
                toast(R.string.es_msg_secure_settings_copied_to_clipboard)
                true
            }
        }
    }

}
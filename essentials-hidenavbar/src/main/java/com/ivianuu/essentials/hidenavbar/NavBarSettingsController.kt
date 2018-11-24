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

package com.ivianuu.essentials.hidenavbar

import android.content.SharedPreferences
import android.os.Bundle
import com.ivianuu.epoxyktx.epoxyController
import com.ivianuu.epoxyprefs.changeListener
import com.ivianuu.epoxyprefs.summaryRes
import com.ivianuu.epoxyprefs.titleRes
import com.ivianuu.essentials.securesettings.SecureSettingsKey
import com.ivianuu.essentials.securesettings.canWriteSecureSettings
import com.ivianuu.essentials.ui.prefs.PrefsController
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.key.key
import com.ivianuu.traveler.navigate
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@Parcelize
class NavBarSettingsKey(
    val showHideNavBarSetting: Boolean
) : ControllerKey(NavBarSettingsController::class)

/**
 * Nav bar settings
 */
class NavBarSettingsController : PrefsController() {

    @Inject lateinit var prefs: NavBarPrefs
    @field:NavBarSharedPrefs @Inject lateinit var navBarSharedPrefs: SharedPreferences

    override val toolbarTitleRes: Int
        get() = R.string.screen_label_nav_bar_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = navBarSharedPrefs // todo ugly
    }

    override fun epoxyController() = epoxyController {
        if (key<NavBarSettingsKey>().showHideNavBarSetting) {
            switchPreference {
                sharedPreferences(navBarSharedPrefs)
                key("hide_nav_bar_enabled")
                summaryRes(R.string.pref_summary_hide_nav_bar_enabled)
                titleRes(R.string.pref_title_hide_nav_bar_enabled)
                changeListener<Boolean> { _, newValue ->
                    if (activity.canWriteSecureSettings() || !newValue) {
                        true
                    } else if (newValue) {
                        travelerRouter.navigate(SecureSettingsKey())
                        false
                    } else {
                        true
                    }
                }
            }
        }

        val settingsEnabled = prefs.hideNavBarEnabled.get()

        checkboxPreference {
            sharedPreferences(navBarSharedPrefs)
            key("rot270_fix")
            summaryRes(R.string.pref_summary_rot270_fix)
            titleRes(R.string.pref_title_rot270_fix)
            enabled(settingsEnabled && !prefs.tabletMode.get())
        }

        checkboxPreference {
            sharedPreferences(navBarSharedPrefs)
            key("tablet_mode")
            summaryRes(R.string.pref_summary_tablet_mode)
            titleRes(R.string.pref_title_tablet_mode)
            enabled(settingsEnabled && !prefs.rot270Fix.get())
        }

        checkboxPreference {
            sharedPreferences(navBarSharedPrefs)
            key("show_nav_bar_screen_off")
            defaultValue(true)
            summaryRes(R.string.pref_summary_show_nav_bar_screen_off)
            titleRes(R.string.pref_title_show_nav_bar_screen_off)
            enabled(settingsEnabled)
        }

        checkboxPreference {
            sharedPreferences(navBarSharedPrefs)
            key("full_overscan")
            defaultValue(true)
            summaryRes(R.string.pref_summary_full_overscan)
            titleRes(R.string.pref_title_full_overscan)
            enabled(settingsEnabled)
        }
    }

}
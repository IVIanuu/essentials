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
import com.ivianuu.director.scopes.destroy
import com.ivianuu.epoxyktx.epoxyController
import com.ivianuu.epoxyprefs.onChange
import com.ivianuu.epoxyprefs.summary
import com.ivianuu.epoxyprefs.title
import com.ivianuu.essentials.securesettings.SecureSettingsKey
import com.ivianuu.essentials.securesettings.canWriteSecureSettings
import com.ivianuu.essentials.ui.prefs.PrefsController
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.key.bindKey
import com.ivianuu.essentials.util.ext.results
import com.ivianuu.scopes.rx.disposeBy
import com.ivianuu.traveler.navigate
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@Parcelize
class NavBarSettingsKey(
    val showMainSwitch: Boolean,
    val showNavBarHidden: Boolean
) : ControllerKey(NavBarSettingsController::class)

/**
 * Nav bar settings
 */
class NavBarSettingsController : PrefsController() {

    @Inject lateinit var prefs: NavBarPrefs
    @field:NavBarSharedPrefs @Inject lateinit var navBarSharedPrefs: SharedPreferences

    override val toolbarTitleRes: Int
        get() = R.string.es_screen_label_nav_bar_settings

    private val key by bindKey<NavBarSettingsKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = navBarSharedPrefs // todo ugly

        travelerRouter.results<Boolean>(RESULT_CODE_MAIN_SWITCH)
            .filter { it }
            .subscribe { prefs.manageNavBar.set(true) }
            .disposeBy(destroy)

        travelerRouter.results<Boolean>(RESULT_CODE_NAV_BAR_HIDDEN)
            .filter { it }
            .subscribe { prefs.navBarHidden.set(true) }
            .disposeBy(destroy)
    }

    override fun epoxyController() = epoxyController {
        if (key.showMainSwitch) {
            switchPreference {
                sharedPreferences(navBarSharedPrefs)
                key("manage_nav_bar")
                title(R.string.es_pref_title_manage_nav_bar)
                onChange<Boolean> { _, newValue ->
                    if (activity.canWriteSecureSettings() || !newValue) {
                        true
                    } else if (newValue) {
                        travelerRouter.navigate(SecureSettingsKey(RESULT_CODE_MAIN_SWITCH))
                        false
                    } else {
                        true
                    }
                }
            }
        }

        val mainSwitchEnabled = prefs.manageNavBar.get()

        if (key.showNavBarHidden) {
            switchPreference {
                sharedPreferences(navBarSharedPrefs)
                key("nav_bar_hidden")
                summary(R.string.es_pref_summary_nav_bar_hidden)
                title(R.string.es_pref_title_nav_bar_hidden)
                enabled(mainSwitchEnabled)
                onChange<Boolean> { _, newValue ->
                    if (activity.canWriteSecureSettings() || !newValue) {
                        true
                    } else if (newValue) {
                        travelerRouter.navigate(SecureSettingsKey(RESULT_CODE_NAV_BAR_HIDDEN))
                        false
                    } else {
                        true
                    }
                }
            }
        }

        checkboxPreference {
            sharedPreferences(navBarSharedPrefs)
            key("rot270_fix")
            summary(R.string.es_pref_summary_rot270_fix)
            title(R.string.es_pref_title_rot270_fix)
            enabled(mainSwitchEnabled && !prefs.tabletMode.get())
        }

        checkboxPreference {
            sharedPreferences(navBarSharedPrefs)
            key("tablet_mode")
            summary(R.string.es_pref_summary_tablet_mode)
            title(R.string.es_pref_title_tablet_mode)
            enabled(mainSwitchEnabled && !prefs.rot270Fix.get())
        }

        checkboxPreference {
            sharedPreferences(navBarSharedPrefs)
            key("show_nav_bar_screen_off")
            defaultValue(true)
            summary(R.string.es_pref_summary_show_nav_bar_screen_off)
            title(R.string.es_pref_title_show_nav_bar_screen_off)
            enabled(mainSwitchEnabled)
        }

        checkboxPreference {
            sharedPreferences(navBarSharedPrefs)
            key("full_overscan")
            summary(R.string.es_pref_summary_full_overscan)
            title(R.string.es_pref_title_full_overscan)
            enabled(mainSwitchEnabled)
        }
    }

    private companion object {
        private const val RESULT_CODE_MAIN_SWITCH = 1234
        private const val RESULT_CODE_NAV_BAR_HIDDEN = 12345
    }
}
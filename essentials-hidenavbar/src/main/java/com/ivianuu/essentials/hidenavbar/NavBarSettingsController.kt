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
import com.ivianuu.epoxyprefs.entries
import com.ivianuu.epoxyprefs.entryValues
import com.ivianuu.epoxyprefs.onChange
import com.ivianuu.epoxyprefs.summary
import com.ivianuu.epoxyprefs.title
import com.ivianuu.essentials.securesettings.SecureSettingsKey
import com.ivianuu.essentials.securesettings.canWriteSecureSettings
import com.ivianuu.essentials.ui.prefs.PrefsController
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.ext.fromEnumPref
import com.ivianuu.essentials.util.ext.fromPref
import com.ivianuu.essentials.util.ext.results
import com.ivianuu.injekt.inject
import com.ivianuu.scopes.rx.disposeBy
import com.ivianuu.traveler.navigate
import kotlinx.android.parcel.Parcelize

@Parcelize
class NavBarSettingsKey(
    val showMainSwitch: Boolean,
    val showNavBarHidden: Boolean
) : ControllerKey(::NavBarSettingsController)

/**
 * Nav bar settings
 */
class NavBarSettingsController : PrefsController() {

    private val prefs by inject<NavBarPrefs>()
    private val navBarSharedPrefs by inject<SharedPreferences>(NavBar)

    override val sharedPreferences: SharedPreferences
        get() = navBarSharedPrefs

    override val toolbarTitleRes: Int
        get() = R.string.es_title_nav_bar_settings

    private val key by inject<NavBarSettingsKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                fromPref(prefs.manageNavBar)
                sharedPreferences(navBarSharedPrefs)
                title(R.string.es_pref_title_manage_nav_bar)
                onChange<Boolean> { _, newValue ->
                    if (!newValue || canWriteSecureSettings()) {
                        true
                    } else if (newValue) {
                        travelerRouter.navigate(
                            SecureSettingsKey(
                                RESULT_CODE_MAIN_SWITCH, true
                            )
                        )
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
                fromPref(prefs.navBarHidden)
                sharedPreferences(navBarSharedPrefs)
                summary(R.string.es_pref_summary_nav_bar_hidden)
                title(R.string.es_pref_title_nav_bar_hidden)
                enabled(mainSwitchEnabled)
                onChange<Boolean> { _, newValue ->
                    if (canWriteSecureSettings() || !newValue) {
                        true
                    } else if (newValue) {
                        travelerRouter.navigate(
                            SecureSettingsKey(
                                RESULT_CODE_NAV_BAR_HIDDEN, true
                            )
                        )
                        false
                    } else {
                        true
                    }
                }
            }
        }

        singleItemListPreference {
            fromEnumPref(prefs.rotationMode)
            sharedPreferences(navBarSharedPrefs)
            summary(R.string.es_pref_summary_nav_bar_rotation_mode)
            title(R.string.es_pref_title_nav_bar_rotation_mode)
            entries(R.array.es_entries_nav_bar_rotation_mode)
            entryValues(R.array.es_values_nav_bar_rotation_mode)
            enabled(mainSwitchEnabled)
        }

        checkboxPreference {
            fromPref(prefs.showNavBarScreenOff)
            sharedPreferences(navBarSharedPrefs)
            summary(R.string.es_pref_summary_show_nav_bar_screen_off)
            title(R.string.es_pref_title_show_nav_bar_screen_off)
            enabled(mainSwitchEnabled)
        }

    }

    private companion object {
        private const val RESULT_CODE_MAIN_SWITCH = 1234
        private const val RESULT_CODE_NAV_BAR_HIDDEN = 12345
    }
}
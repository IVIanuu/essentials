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

package com.ivianuu.essentials.twilight

import com.airbnb.epoxy.EpoxyController
import com.ivianuu.epoxyprefs.RadioButtonPreference
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.prefs.PrefsController
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.andTrue
import com.ivianuu.injekt.inject
import kotlinx.android.parcel.Parcelize

@Parcelize
object TwilightSettingsKey : ControllerKey(::TwilightSettingsController)

class TwilightSettingsController : PrefsController() {

    override val toolbarTitleRes: Int
        get() = R.string.es_title_twilight

    private val prefs by inject<TwilightPrefs>()

    override fun epoxyController() = epoxyController {
        TwilightModePreference(
            mode = TwilightMode.SYSTEM,
            descRes = R.string.es_twilight_mode_system_desc,
            titleRes = R.string.es_twilight_mode_system
        )
        TwilightModePreference(
            mode = TwilightMode.LIGHT,
            descRes = R.string.es_twilight_mode_light_desc,
            titleRes = R.string.es_twilight_mode_light
        )
        TwilightModePreference(
            mode = TwilightMode.DARK,
            descRes = R.string.es_twilight_mode_dark_desc,
            titleRes = R.string.es_twilight_mode_dark
        )
        TwilightModePreference(
            mode = TwilightMode.BATTERY,
            descRes = R.string.es_twilight_mode_battery_desc,
            titleRes = R.string.es_twilight_mode_battery
        )
        TwilightModePreference(
            mode = TwilightMode.TIME,
            descRes = R.string.es_twilight_mode_time_desc,
            titleRes = R.string.es_twilight_mode_time
        )
    }

    private fun EpoxyController.TwilightModePreference(
        mode: TwilightMode,
        descRes: Int,
        titleRes: Int
    ) {
        RadioButtonPreference {
            key(mode.value)
            titleRes(titleRes)
            summaryRes(descRes)
            defaultValue(prefs.twilightMode.get() == mode)
            isPersistent(false)
            onClick { prefs.twilightMode.set(mode).andTrue() }
        }
    }

}
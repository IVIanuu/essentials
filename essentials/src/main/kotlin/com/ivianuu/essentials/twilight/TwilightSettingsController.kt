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

package com.ivianuu.essentials.twilight

import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.epoxy.RadioButtonGroup
import com.ivianuu.essentials.ui.epoxy.RadioButtonItem
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.prefs.PrefsController
import com.ivianuu.injekt.Inject

val twilightSettingsRoute = controllerRoute<TwilightSettingsController>()

@Inject
class TwilightSettingsController(private val prefs: TwilightPrefs) : PrefsController() {

    override val toolbarTitleRes: Int
        get() = R.string.es_title_twilight

    override fun epoxyController() = epoxyController {
        RadioButtonGroup(
            pref = prefs.twilightMode,
            items = listOf(
                RadioButtonItem(
                    value = TwilightMode.SYSTEM,
                    titleRes = R.string.es_twilight_mode_system,
                    textRes = R.string.es_twilight_mode_system_desc
                ),
                RadioButtonItem(
                    value = TwilightMode.LIGHT,
                    titleRes = R.string.es_twilight_mode_light,
                    textRes = R.string.es_twilight_mode_light_desc
                ),
                RadioButtonItem(
                    value = TwilightMode.DARK,
                    titleRes = R.string.es_twilight_mode_dark,
                    textRes = R.string.es_twilight_mode_dark_desc
                ),
                RadioButtonItem(
                    value = TwilightMode.BATTERY,
                    titleRes = R.string.es_twilight_mode_battery,
                    textRes = R.string.es_twilight_mode_battery_desc
                ),
                RadioButtonItem(
                    value = TwilightMode.TIME,
                    titleRes = R.string.es_twilight_mode_time,
                    textRes = R.string.es_twilight_mode_time_desc
                )
            )
        )
    }

}
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

import com.github.ajalt.timberkt.d
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.common.RecyclerView
import com.ivianuu.compose.common.Route
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.AppBar
import com.ivianuu.essentials.ui.compose.ListItem
import com.ivianuu.essentials.ui.compose.RadioButton
import com.ivianuu.essentials.ui.compose.Scaffold
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.prefs.Prefs

fun TwilightSettingsRoute() = Route {
    Prefs {
        d { "rebuild prefs" }
        Scaffold(
            appBar = { AppBar(titleRes = R.string.es_title_twilight) },
            content = {
                RecyclerView {
                    TwilightModeItem(
                        mode = TwilightMode.SYSTEM,
                        titleRes = R.string.es_twilight_mode_system,
                        textRes = R.string.es_twilight_mode_system_desc
                    )

                    TwilightModeItem(
                        mode = TwilightMode.LIGHT,
                        titleRes = R.string.es_twilight_mode_light,
                        textRes = R.string.es_twilight_mode_light_desc
                    )

                    TwilightModeItem(
                        mode = TwilightMode.DARK,
                        titleRes = R.string.es_twilight_mode_dark,
                        textRes = R.string.es_twilight_mode_dark_desc
                    )

                    TwilightModeItem(
                        mode = TwilightMode.BATTERY,
                        titleRes = R.string.es_twilight_mode_battery,
                        textRes = R.string.es_twilight_mode_battery_desc
                    )

                    TwilightModeItem(
                        mode = TwilightMode.TIME,
                        titleRes = R.string.es_twilight_mode_time,
                        textRes = R.string.es_twilight_mode_time_desc
                    )
                }
            }
        )
    }
}

private inline fun ComponentComposition.TwilightModeItem(
    mode: TwilightMode,
    titleRes: Int,
    textRes: Int
) {
    val prefs = inject<TwilightPrefs>()

    group {
        ListItem(
            titleRes = titleRes,
            textRes = textRes,
            trailingAction = {
                RadioButton(
                    value = prefs.twilightMode.get() == mode,
                    onClick = {}
                )
            },
            onClick = { prefs.twilightMode.set(mode) }
        )
    }
}
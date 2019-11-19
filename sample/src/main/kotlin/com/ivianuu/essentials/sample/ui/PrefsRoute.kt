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

package com.ivianuu.essentials.sample.ui

import androidx.ui.core.Text
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollableList
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.prefs.CheckboxPreference
import com.ivianuu.essentials.ui.compose.prefs.Dependency
import com.ivianuu.essentials.ui.compose.prefs.MultiChoiceListPreference
import com.ivianuu.essentials.ui.compose.prefs.PreferenceSubheader
import com.ivianuu.essentials.ui.compose.prefs.Prefs
import com.ivianuu.essentials.ui.compose.prefs.RadioButtonPreference
import com.ivianuu.essentials.ui.compose.prefs.SingleChoiceListPreference
import com.ivianuu.essentials.ui.compose.prefs.SliderPreference
import com.ivianuu.essentials.ui.compose.prefs.SwitchPreference
import com.ivianuu.essentials.ui.compose.prefs.TextInputPreference
import com.ivianuu.essentials.ui.compose.prefs.unitValueTextProvider
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.vertical
import com.ivianuu.essentials.util.UnitValueTextProvider
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.boolean
import com.ivianuu.kprefs.int
import com.ivianuu.kprefs.string
import com.ivianuu.kprefs.stringSet

val prefsRoute = composeControllerRoute(
    options = controllerRouteOptions().vertical()
) {
    Scaffold(
        topAppBar = { EsTopAppBar(title = "Prefs") },
        body = {
            Prefs {
                ScrollableList {
                    val prefs = inject<KPrefs>()

                    SwitchPreference(
                        pref = prefs.boolean("switch"),
                        title = { Text("Switch") }
                    )

                    val dependencies = listOf(
                        Dependency(
                            pref = prefs.boolean("switch"),
                            value = true
                        )
                    )

                    PreferenceSubheader(text = "Category", dependencies = dependencies)

                    CheckboxPreference(
                        pref = prefs.boolean("checkbox"),
                        title = { Text("Checkbox") },
                        summary = { Text("This is a checkbox preference") },
                        dependencies = dependencies
                    )

                    RadioButtonPreference(
                        pref = prefs.boolean("radio_button"),
                        title = { Text("Radio Button") },
                        summary = { Text("This is a radio button preference") },
                        dependencies = dependencies
                    )

                    SliderPreference(
                        pref = prefs.int("slider"),
                        title = { Text("Slider") },
                        summary = { Text("This is a slider preference") },
                        valueText = unitValueTextProvider(UnitValueTextProvider.Unit.Dp),
                        dependencies = dependencies
                    )

                    PreferenceSubheader(text = "Dialogs", dependencies = dependencies)

                    TextInputPreference(
                        pref = prefs.string("text_input"),
                        title = { Text("Text input") },
                        summary = { Text("This is a text input preference") },
                        allowEmpty = false,
                        dependencies = dependencies
                    )

                    MultiChoiceListPreference(
                        pref = prefs.stringSet("multi_select_list", setOf("A", "B")),
                        items = listOf(
                            MultiChoiceListPreference.Item("A"),
                            MultiChoiceListPreference.Item("B"),
                            MultiChoiceListPreference.Item("C")
                        ),
                        title = { Text("Multi select list") },
                        summary = { Text("This is a multi select list preference") },
                        dependencies = dependencies
                    )

                    SingleChoiceListPreference(
                        pref = prefs.string("single_item_list", "C"),
                        items = listOf(
                            SingleChoiceListPreference.Item("A"),
                            SingleChoiceListPreference.Item("B"),
                            SingleChoiceListPreference.Item("C")
                        ),
                        title = { Text("Single item list") },
                        summary = { Text("This is a single item list preference") },
                        dependencies = dependencies
                    )
                }
            }
        }
    )
}
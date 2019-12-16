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

import com.ivianuu.essentials.store.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.prefs.boolean
import com.ivianuu.essentials.store.prefs.int
import com.ivianuu.essentials.store.prefs.string
import com.ivianuu.essentials.store.prefs.stringSet
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.layout.ScrollableList
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.navigation.Route
import com.ivianuu.essentials.ui.compose.prefs.CheckboxPreference
import com.ivianuu.essentials.ui.compose.prefs.Dependency
import com.ivianuu.essentials.ui.compose.prefs.MultiChoiceListPreference
import com.ivianuu.essentials.ui.compose.prefs.PreferenceSubheader
import com.ivianuu.essentials.ui.compose.prefs.RadioButtonPreference
import com.ivianuu.essentials.ui.compose.prefs.SingleChoiceListPreference
import com.ivianuu.essentials.ui.compose.prefs.SliderPreference
import com.ivianuu.essentials.ui.compose.prefs.SwitchPreference
import com.ivianuu.essentials.ui.compose.prefs.TextInputPreference
import com.ivianuu.essentials.ui.compose.prefs.unitValueTextProvider
import com.ivianuu.essentials.util.UnitValueTextProvider

val PrefsRoute = Route {
    Scaffold(
        topAppBar = { EsTopAppBar(title = "Prefs") },
        body = {
            ScrollableList {
                val boxFactory = inject<PrefBoxFactory>()

                SwitchPreference(
                    box = boxFactory.boolean("switch"),
                    title = "Switch"
                )

                val dependencies = listOf(
                    Dependency(
                        box = boxFactory.boolean("switch"),
                        value = true
                    )
                )

                PreferenceSubheader(text = "Category", dependencies = dependencies)

                CheckboxPreference(
                    box = boxFactory.boolean("checkbox"),
                    dependencies = dependencies,
                    title = "Checkbox",
                    summary = "This is a checkbox preference"
                )

                RadioButtonPreference(
                    box = boxFactory.boolean("radio_button"),
                    dependencies = dependencies,
                    title = "Radio Button",
                    summary = "This is a radio button preference"
                )

                SliderPreference(
                    box = boxFactory.int("slider"),
                    dependencies = dependencies,
                    title = "Slider",
                    summary = "This is a slider preference",
                    valueText = unitValueTextProvider(UnitValueTextProvider.Unit.Dp)
                )

                PreferenceSubheader(text = "Dialogs", dependencies = dependencies)

                TextInputPreference(
                    box = boxFactory.string("text_input"),
                    dependencies = dependencies,
                    title = "Text input",
                    summary = "This is a text input preference",
                    allowEmpty = false
                )

                MultiChoiceListPreference(
                    box = boxFactory.stringSet("multi_select_list", setOf("A", "B", "C")),
                    dependencies = dependencies,
                    title = "Multi select list",
                    summary = "This is a multi select list preference",
                    items = listOf(
                        MultiChoiceListPreference.Item("A", "A"),
                        MultiChoiceListPreference.Item("B", "B"),
                        MultiChoiceListPreference.Item("C", "C")
                    )
                )

                SingleChoiceListPreference(
                    box = boxFactory.string("single_item_list", "C"),
                    dependencies = dependencies,
                    title = "Single item list",
                    summary = "This is a single item list preference",
                    items = listOf(
                        SingleChoiceListPreference.Item("A", "A"),
                        SingleChoiceListPreference.Item("B", "B"),
                        SingleChoiceListPreference.Item("C", "C")
                    )
                )
            }
        }
    )
}
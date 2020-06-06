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

import androidx.ui.core.Modifier
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import com.ivianuu.essentials.store.android.duration
import com.ivianuu.essentials.store.android.prefs.PrefBoxFactory
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.prefs.CheckboxPreference
import com.ivianuu.essentials.ui.prefs.ClipboardPreference
import com.ivianuu.essentials.ui.prefs.ColorPreference
import com.ivianuu.essentials.ui.prefs.Dependency
import com.ivianuu.essentials.ui.prefs.DurationSliderPreference
import com.ivianuu.essentials.ui.prefs.IntSliderPreference
import com.ivianuu.essentials.ui.prefs.MultiChoiceListPreference
import com.ivianuu.essentials.ui.prefs.RadioButtonPreference
import com.ivianuu.essentials.ui.prefs.SingleChoiceListPreference
import com.ivianuu.essentials.ui.prefs.SwitchPreference
import com.ivianuu.essentials.ui.prefs.TextInputPreference
import com.ivianuu.essentials.ui.prefs.preferenceDependencies
import kotlin.time.hours
import kotlin.time.minutes

val PrefsRoute = Route {
    Scaffold(
        topAppBar = { TopAppBar(title = { Text("Prefs") }) },
        body = {
            VerticalScroller {
                val boxFactory = inject<PrefBoxFactory>()

                SwitchPreference(
                    box = boxFactory.create("switch", false),
                    title = { Text("Switch") }
                )

                val dependenciesModifier = Modifier.preferenceDependencies(
                    Dependency(
                        box = boxFactory.create("switch", false),
                        value = true
                    )
                )

                Subheader(modifier = dependenciesModifier) { Text("Category") }

                CheckboxPreference(
                    box = boxFactory.create("checkbox", false),
                    modifier = dependenciesModifier,
                    title = { Text("Checkbox") },
                    summary = { Text("This is a checkbox preference") }
                )

                RadioButtonPreference(
                    box = boxFactory.create("radio_button", false),
                    modifier = dependenciesModifier,
                    title = { Text("Radio Button") },
                    summary = { Text("This is a radio button preference") }
                )

                IntSliderPreference(
                    box = boxFactory.create("slider", 50),
                    modifier = dependenciesModifier,
                    title = { Text("Slider") },
                    summary = { Text("This is a slider preference") },
                    steps = 10,
                    valueRange = 0..100
                )

                DurationSliderPreference(
                    box = boxFactory.duration("slider_dur", 33.minutes),
                    modifier = dependenciesModifier,
                    title = { Text("Slider duration") },
                    summary = { Text("This is a slider preference") },
                    steps = 10,
                    valueRange = 1.minutes..1.hours
                )

                Subheader(modifier = dependenciesModifier) {
                    Text("Dialogs")
                }

                TextInputPreference(
                    box = boxFactory.create("text_input", ""),
                    modifier = dependenciesModifier,
                    title = { Text("Text input") },
                    summary = { Text("This is a text input preference") },
                    allowEmpty = false
                )

                ColorPreference(
                    box = boxFactory.create("color", Color.Red),
                    modifier = dependenciesModifier,
                    title = { Text("Color") },
                    summary = { Text("This is a color preference") }
                )

                MultiChoiceListPreference(
                    box = boxFactory.create("multi_select_list", setOf("A", "B", "C")),
                    modifier = dependenciesModifier,
                    title = { Text("Multi select list") },
                    summary = { Text("This is a multi select list preference") },
                    items = listOf(
                        MultiChoiceListPreference.Item("A", "A"),
                        MultiChoiceListPreference.Item("B", "B"),
                        MultiChoiceListPreference.Item("C", "C")
                    )
                )

                SingleChoiceListPreference(
                    box = boxFactory.create("single_item_list", "C"),
                    modifier = dependenciesModifier,
                    title = { Text("Single item list") },
                    summary = { Text("This is a single item list preference") },
                    items = listOf(
                        SingleChoiceListPreference.Item("A", "A"),
                        SingleChoiceListPreference.Item("B", "B"),
                        SingleChoiceListPreference.Item("C", "C")
                    )
                )

                ClipboardPreference(
                    title = { Text("Clipboard") },
                    summary = { Text("This is a clipboard preference") },
                    clipboardText = { "cool clip" },
                    modifier = dependenciesModifier
                )
            }
        }
    )
}
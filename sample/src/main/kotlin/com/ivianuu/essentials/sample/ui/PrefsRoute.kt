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

import androidx.ui.graphics.Color
import com.ivianuu.essentials.android.ui.common.Scroller
import com.ivianuu.essentials.android.ui.core.Text
import com.ivianuu.essentials.android.ui.injekt.inject
import com.ivianuu.essentials.android.ui.layout.Column
import com.ivianuu.essentials.android.ui.material.Scaffold
import com.ivianuu.essentials.android.ui.material.TopAppBar
import com.ivianuu.essentials.android.ui.navigation.Route
import com.ivianuu.essentials.android.ui.prefs.CheckboxPreference
import com.ivianuu.essentials.android.ui.prefs.ClipboardPreference
import com.ivianuu.essentials.android.ui.prefs.ColorPreference
import com.ivianuu.essentials.android.ui.prefs.Dependency
import com.ivianuu.essentials.android.ui.prefs.DurationSliderPreference
import com.ivianuu.essentials.android.ui.prefs.IntSliderPreference
import com.ivianuu.essentials.android.ui.prefs.MultiChoiceListPreference
import com.ivianuu.essentials.android.ui.prefs.PreferenceSubheader
import com.ivianuu.essentials.android.ui.prefs.RadioButtonPreference
import com.ivianuu.essentials.android.ui.prefs.SingleChoiceListPreference
import com.ivianuu.essentials.android.ui.prefs.SwitchPreference
import com.ivianuu.essentials.android.ui.prefs.TextInputPreference
import com.ivianuu.essentials.store.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.prefs.boolean
import com.ivianuu.essentials.store.prefs.color
import com.ivianuu.essentials.store.prefs.duration
import com.ivianuu.essentials.store.prefs.int
import com.ivianuu.essentials.store.prefs.string
import com.ivianuu.essentials.store.prefs.stringSet
import kotlin.time.hours
import kotlin.time.minutes

val PrefsRoute = Route {
    Scaffold(
        topAppBar = { TopAppBar(title = "Prefs") },
        body = {
            Scroller {
                Column {
                    val boxFactory = inject<PrefBoxFactory>()

                    SwitchPreference(
                        box = boxFactory.boolean("switch"),
                        title = { Text("Switch") }
                    )

                    val dependencies = listOf(
                        Dependency(
                            box = boxFactory.boolean("switch"),
                            value = true
                        )
                    )

                    PreferenceSubheader(dependencies = dependencies) {
                        Text("Category")
                    }

                    CheckboxPreference(
                        box = boxFactory.boolean("checkbox"),
                        dependencies = dependencies,
                        title = { Text("Checkbox") },
                        summary = { Text("This is a checkbox preference") }
                    )

                    RadioButtonPreference(
                        box = boxFactory.boolean("radio_button"),
                        dependencies = dependencies,
                        title = { Text("Radio Button") },
                        summary = { Text("This is a radio button preference") }
                    )

                    IntSliderPreference(
                        box = boxFactory.int("slider"),
                        dependencies = dependencies,
                        title = { Text("Slider") },
                        summary = { Text("This is a slider preference") },
                        steps = 10,
                        valueRange = 0..100
                    )

                    DurationSliderPreference(
                        box = boxFactory.duration("slider_dur", 33.minutes),
                        dependencies = dependencies,
                        title = { Text("Slider duration") },
                        summary = { Text("This is a slider preference") },
                        steps = 10,
                        valueRange = 1.minutes..1.hours
                    )

                    PreferenceSubheader(dependencies = dependencies) {
                        Text("Dialogs")
                    }

                    TextInputPreference(
                        box = boxFactory.string("text_input"),
                        dependencies = dependencies,
                        title = { Text("Text input") },
                        summary = { Text("This is a text input preference") },
                        allowEmpty = false
                    )

                    ColorPreference(
                        box = boxFactory.color("color", Color.Red),
                        dependencies = dependencies,
                        title = { Text("Color") },
                        summary = { Text("This is a color preference") }
                    )

                    MultiChoiceListPreference(
                        box = boxFactory.stringSet("multi_select_list", setOf("A", "B", "C")),
                        dependencies = dependencies,
                        title = { Text("Multi select list") },
                        summary = { Text("This is a multi select list preference") },
                        items = listOf(
                            MultiChoiceListPreference.Item("A", "A"),
                            MultiChoiceListPreference.Item("B", "B"),
                            MultiChoiceListPreference.Item("C", "C")
                        )
                    )

                    SingleChoiceListPreference(
                        box = boxFactory.string("single_item_list", "C"),
                        dependencies = dependencies,
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
                        dependencies = dependencies
                    )
                }
            }
        }
    )
}
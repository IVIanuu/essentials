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

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import com.ivianuu.essentials.store.android.prefs.PrefBoxFactory
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.essentials.ui.prefs.ClipboardListItem
import com.ivianuu.essentials.ui.prefs.ColorDialogListItem
import com.ivianuu.essentials.ui.prefs.Dependency
import com.ivianuu.essentials.ui.prefs.DurationSliderListItem
import com.ivianuu.essentials.ui.prefs.IntSliderListItem
import com.ivianuu.essentials.ui.prefs.MultiChoiceDialogListItem
import com.ivianuu.essentials.ui.prefs.RadioButtonListItem
import com.ivianuu.essentials.ui.prefs.SingleChoiceDialogListItem
import com.ivianuu.essentials.ui.prefs.SliderValueText
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.essentials.ui.prefs.TextInputDialogListItem
import com.ivianuu.essentials.ui.prefs.preferenceDependencies
import com.ivianuu.injekt.Transient
import kotlin.time.hours
import kotlin.time.minutes

@Transient
class PrefsPage(private val boxFactory: PrefBoxFactory) {
    @Composable
    operator fun invoke() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Prefs") }) }
        ) {
            VerticalScroller {
                SwitchListItem(
                    dataStore = boxFactory.create("switch") { false },
                    title = { Text("Switch") }
                )

                val dependenciesModifier = Modifier.preferenceDependencies(
                    Dependency(
                        dataStore = boxFactory.create("switch") { false },
                        value = true
                    )
                )

                Subheader(modifier = dependenciesModifier) { Text("Category") }

                CheckboxListItem(
                    dataStore = boxFactory.create("checkbox") { false },
                    modifier = dependenciesModifier,
                    title = { Text("Checkbox") },
                    subtitle = { Text("This is a checkbox preference") }
                )

                RadioButtonListItem(
                    dataStore = boxFactory.create("radio_button") { false },
                    modifier = dependenciesModifier,
                    title = { Text("Radio Button") },
                    subtitle = { Text("This is a radio button preference") }
                )

                IntSliderListItem(
                    dataStore = boxFactory.create("slider") { 50 },
                    modifier = dependenciesModifier,
                    title = { Text("Slider") },
                    subtitle = { Text("This is a slider preference") },
                    steps = 10,
                    valueRange = 0..100,
                    valueText = { SliderValueText(it) }
                )

                DurationSliderListItem(
                    dataStore = boxFactory.duration("slider_dur") { 33.minutes },
                    modifier = dependenciesModifier,
                    title = { Text("Slider duration") },
                    subtitle = { Text("This is a slider preference") },
                    steps = 10,
                    valueRange = 1.minutes..1.hours
                )

                Subheader(modifier = dependenciesModifier) {
                    Text("Dialogs")
                }

                TextInputDialogListItem(
                    dataStore = boxFactory.create("text_input") { "" },
                    modifier = dependenciesModifier,
                    title = { Text("Text input") },
                    subtitle = { Text("This is a text input preference") },
                    allowEmpty = false
                )

                ColorDialogListItem(
                    dataStore = boxFactory.color("color") { Color.Red },
                    modifier = dependenciesModifier,
                    title = { Text("Color") },
                    subtitle = { Text("This is a color preference") }
                )

                MultiChoiceDialogListItem(
                    dataStore = boxFactory.create("multi_select_list") { setOf("A", "B", "C") },
                    modifier = dependenciesModifier,
                    title = { Text("Multi select list") },
                    subtitle = { Text("This is a multi select list preference") },
                    items = listOf(
                        MultiChoiceDialogListItem.Item("A", "A"),
                        MultiChoiceDialogListItem.Item("B", "B"),
                        MultiChoiceDialogListItem.Item("C", "C")
                    )
                )

                SingleChoiceDialogListItem(
                    dataStore = boxFactory.create("single_item_list") { "C" },
                    modifier = dependenciesModifier,
                    title = { Text("Single item list") },
                    subtitle = { Text("This is a single item list preference") },
                    items = listOf(
                        SingleChoiceDialogListItem.Item("A", "A"),
                        SingleChoiceDialogListItem.Item("B", "B"),
                        SingleChoiceDialogListItem.Item("C", "C")
                    )
                )

                ClipboardListItem(
                    title = { Text("Clipboard") },
                    subtitle = { Text("This is a clipboard preference") },
                    clipboardText = { "cool clip" },
                    modifier = dependenciesModifier
                )
            }
        }
    }
}

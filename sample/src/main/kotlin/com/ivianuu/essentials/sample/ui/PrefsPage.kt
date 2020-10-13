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

import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.android.color
import com.ivianuu.essentials.datastore.android.duration
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.material.incrementingStepPolicy
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.essentials.ui.prefs.ClipboardListItem
import com.ivianuu.essentials.ui.prefs.ColorDialogListItem
import com.ivianuu.essentials.ui.prefs.DurationSliderListItem
import com.ivianuu.essentials.ui.prefs.IntSliderListItem
import com.ivianuu.essentials.ui.prefs.MultiChoiceDialogListItem
import com.ivianuu.essentials.ui.prefs.RadioButtonListItem
import com.ivianuu.essentials.ui.prefs.SingleChoiceDialogListItem
import com.ivianuu.essentials.ui.prefs.SliderValueText
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.essentials.ui.prefs.TextInputDialogListItem
import com.ivianuu.essentials.ui.prefs.preferenceDependencies
import com.ivianuu.essentials.ui.prefs.requiresValue
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlin.time.hours
import kotlin.time.milliseconds
import kotlin.time.minutes

@FunBinding
@Composable
fun PrefsPage(prefs: Prefs) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Prefs") }) }
    ) {
        InsettingScrollableColumn {
            SwitchListItem(
                dataStore = prefs.switch,
                title = { Text("Switch") }
            )

            val dependenciesModifier = Modifier.preferenceDependencies(
                prefs.switch requiresValue true
            )

            Subheader(modifier = dependenciesModifier) { Text("Category") }

            CheckboxListItem(
                dataStore = prefs.checkbox,
                modifier = dependenciesModifier,
                title = { Text("Checkbox") },
                subtitle = { Text("This is a checkbox preference") }
            )

            RadioButtonListItem(
                dataStore = prefs.radioButton,
                modifier = dependenciesModifier,
                title = { Text("Radio Button") },
                subtitle = { Text("This is a radio button preference") }
            )

            IntSliderListItem(
                dataStore = prefs.slider,
                modifier = dependenciesModifier,
                title = { Text("Slider") },
                subtitle = { Text("This is a slider preference") },
                stepPolicy = incrementingStepPolicy(5),
                valueRange = 0..100,
                valueText = { SliderValueText(it) }
            )

            DurationSliderListItem(
                dataStore = prefs.durationSlider,
                modifier = dependenciesModifier,
                title = { Text("Slider duration") },
                subtitle = { Text("This is a slider preference") },
                stepPolicy = incrementingStepPolicy(1.minutes),
                valueRange = 1.minutes..1.hours,
                valueText = { SliderValueText(it) }
            )

            Subheader(modifier = dependenciesModifier) {
                Text("Dialogs")
            }

            TextInputDialogListItem(
                dataStore = prefs.textInput,
                modifier = dependenciesModifier,
                title = { Text("Text input") },
                subtitle = { Text("This is a text input preference") },
                allowEmpty = false
            )

            ColorDialogListItem(
                dataStore = prefs.color,
                modifier = dependenciesModifier,
                title = { Text("Color") },
                subtitle = { Text("This is a color preference") }
            )

            MultiChoiceDialogListItem(
                dataStore = prefs.multiChoice,
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
                dataStore = prefs.singleChoice,
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

@Binding(ApplicationComponent::class)
class Prefs(factory: DiskDataStoreFactory) {
    val switch = factory.create("switch") { false }
    val checkbox = factory.create("checkbox") { false }
    val radioButton = factory.create("radio_button") { false }
    val slider = factory.create("slider") { 50 }
    val durationSlider =
        factory.duration("duration_slider") { 33.milliseconds }
    val textInput = factory.create("text_input") { "" }
    val color = factory.color("color") { Color.Red }
    val multiChoice = factory.create("multi_choice") { setOf("A", "B", "C") }
    val singleChoice = factory.create("single_choice") { "C" }
}

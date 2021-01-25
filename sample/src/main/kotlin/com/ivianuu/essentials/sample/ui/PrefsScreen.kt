/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ivianuu.essentials.datastore.android.PrefModule
import com.ivianuu.essentials.datastore.android.dispatchPrefUpdate
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.common.interactive
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.material.incrementingStepPolicy
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.essentials.ui.prefs.ColorDialogListItem
import com.ivianuu.essentials.ui.prefs.IntSliderListItem
import com.ivianuu.essentials.ui.prefs.MultiChoiceDialogListItem
import com.ivianuu.essentials.ui.prefs.RadioButtonListItem
import com.ivianuu.essentials.ui.prefs.SingleChoiceDialogListItem
import com.ivianuu.essentials.ui.prefs.SliderValueText
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.essentials.ui.prefs.TextInputDialogListItem
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.Module
import com.squareup.moshi.JsonClass

@HomeItemBinding
@Given
val prefsHomeItem = HomeItem("Prefs") { PrefsKey() }

class PrefsKey

@KeyUiBinding<PrefsKey>
@GivenFun
@Composable
fun PrefsScreen(
    @Given prefs: @UiState SamplePrefs,
    @Given dispatchUpdate: dispatchPrefUpdate<SamplePrefs>,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Prefs") }) }
    ) {
        InsettingScrollableColumn {
            SwitchListItem(
                value = prefs.switch,
                onValueChange = { dispatchUpdate { copy(switch = it) } },
                title = { Text("Switch") }
            )

            Subheader(modifier = Modifier.interactive(prefs.switch)) { Text("Category") }

            CheckboxListItem(
                value = prefs.checkbox,
                onValueChange = { dispatchUpdate { copy(checkbox = it) } },
                modifier = Modifier.interactive(prefs.switch),
                title = { Text("Checkbox") },
                subtitle = { Text("This is a checkbox preference") }
            )

            RadioButtonListItem(
                value = prefs.radioButton,
                onValueChange = { dispatchUpdate { copy(radioButton = it) } },
                modifier = Modifier.interactive(prefs.switch),
                title = { Text("Radio Button") },
                subtitle = { Text("This is a radio button preference") }
            )

            IntSliderListItem(
                value = prefs.slider,
                onValueChange = { dispatchUpdate { copy(slider = it) } },
                modifier = Modifier.interactive(prefs.switch),
                title = { Text("Slider") },
                subtitle = { Text("This is a slider preference") },
                stepPolicy = incrementingStepPolicy(5),
                valueRange = 0..100,
                valueText = { SliderValueText(it) }
            )

            Subheader(modifier = Modifier.interactive(prefs.switch)) {
                Text("Dialogs")
            }

            TextInputDialogListItem(
                value = prefs.textInput,
                onValueChange = { dispatchUpdate { copy(textInput = it) } },
                modifier = Modifier.interactive(prefs.switch),
                title = { Text("Text input") },
                subtitle = { Text("This is a text input preference") },
                allowEmpty = false
            )

            ColorDialogListItem(
                value = Color(prefs.color),
                onValueChange = { dispatchUpdate { copy(color = it.toArgb()) } },
                modifier = Modifier.interactive(prefs.switch),
                title = { Text("Color") },
                subtitle = { Text("This is a color preference") }
            )

            MultiChoiceDialogListItem(
                value = prefs.multiChoice,
                onValueChange = { dispatchUpdate { copy(multiChoice = it) } },
                modifier = Modifier.interactive(prefs.switch),
                title = { Text("Multi select list") },
                subtitle = { Text("This is a multi select list preference") },
                items = listOf(
                    MultiChoiceDialogListItem.Item("A", "A"),
                    MultiChoiceDialogListItem.Item("B", "B"),
                    MultiChoiceDialogListItem.Item("C", "C")
                )
            )

            SingleChoiceDialogListItem(
                value = prefs.singleChoice,
                modifier = Modifier.interactive(prefs.switch),
                onValueChange = { dispatchUpdate { copy(singleChoice = it) } },
                title = { Text("Single item list") },
                subtitle = { Text("This is a single item list preference") },
                items = listOf(
                    SingleChoiceDialogListItem.Item("A", "A"),
                    SingleChoiceDialogListItem.Item("B", "B"),
                    SingleChoiceDialogListItem.Item("C", "C")
                )
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class SamplePrefs(
    val switch: Boolean = false,
    val checkbox: Boolean = false,
    val radioButton: Boolean = false,
    val slider: Int = 50,
    val textInput: String = "",
    val color: Int = Color.Red.toArgb(),
    val multiChoice: Set<String> = setOf("A", "B", "C"),
    val singleChoice: String = "C",
)

@Module val samplePrefsModule = PrefModule<SamplePrefs>("sample_prefs")
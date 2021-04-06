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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ivianuu.essentials.android.prefs.PrefStoreModule
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.data.ValueAction
import com.ivianuu.essentials.data.update
import com.ivianuu.essentials.store.Sink
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.ui.UiGivenScope
import com.ivianuu.essentials.ui.common.interactive
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.dialog.ColorPickerKey
import com.ivianuu.essentials.ui.dialog.MultiChoiceListKey
import com.ivianuu.essentials.ui.dialog.SingleChoiceListKey
import com.ivianuu.essentials.ui.dialog.TextInputKey
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.material.incrementingStepPolicy
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.pushForResult
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.essentials.ui.prefs.ColorListItem
import com.ivianuu.essentials.ui.prefs.IntSliderListItem
import com.ivianuu.essentials.ui.prefs.RadioButtonListItem
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Given
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Given
val prefsHomeItem = HomeItem("Prefs") { PrefsKey() }

class PrefsKey : Key<Nothing>

@Given
fun prefsUi(
    @Given navigator: Sink<NavigationAction>,
    @Given prefStore: Store<SamplePrefs, ValueAction<SamplePrefs>>,
    @Given scope: ScopeCoroutineScope<UiGivenScope>
): KeyUi<PrefsKey> = {
    val prefs by prefStore.collectAsState(remember { SamplePrefs() })
    Scaffold(
        topBar = { TopAppBar(title = { Text("Prefs") }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                SwitchListItem(
                    value = prefs.switch,
                    onValueChange = {
                        prefStore.update { copy(switch = it) }
                    },
                    title = { Text("Switch") }
                )
            }
            item {
                Subheader(modifier = Modifier.interactive(prefs.switch)) { Text("Category") }
            }
            item {
                CheckboxListItem(
                    value = prefs.checkbox,
                    onValueChange = {
                        prefStore.update { copy(checkbox = it) }
                    },
                    modifier = Modifier.interactive(prefs.switch),
                    title = { Text("Checkbox") },
                    subtitle = { Text("This is a checkbox preference") }
                )

            }
            item {
                RadioButtonListItem(
                    value = prefs.radioButton,
                    onValueChange = {
                        prefStore.update { copy(radioButton = it) }
                    },
                    modifier = Modifier.interactive(prefs.switch),
                    title = { Text("Radio Button") },
                    subtitle = { Text("This is a radio button preference") }
                )
            }
            item {
                IntSliderListItem(
                    value = prefs.slider,
                    onValueChange = {
                        prefStore.update { copy(slider = it) }
                    },
                    modifier = Modifier.interactive(prefs.switch),
                    title = { Text("Slider") },
                    subtitle = { Text("This is a slider preference") },
                    stepPolicy = incrementingStepPolicy(5),
                    valueRange = 0..100,
                    valueText = { Text(it.toString()) }
                )
            }
            item {
                Subheader(modifier = Modifier.interactive(prefs.switch)) {
                    Text("Dialogs")
                }
            }
            item {
                ListItem(
                    modifier = Modifier.interactive(prefs.switch),
                    title = { Text("Text input") },
                    subtitle = { Text("This is a text input preference") },
                    onClick = {
                        scope.launch {
                            val newTextInput = navigator.pushForResult(
                                TextInputKey(
                                    initial = prefs.textInput,
                                    label = "Input",
                                    title = "Text input",
                                    allowEmpty = false
                                )
                            ) ?: return@launch
                            prefStore.update { copy(textInput = newTextInput) }
                        }
                    }
                )
            }
            item {
                ColorListItem(
                    value = Color(prefs.color),
                    onValueChangeRequest = {
                        scope.launch {
                            val newColor = navigator.pushForResult(
                                ColorPickerKey(
                                    initialColor = Color(prefs.color),
                                    title = "Color"
                                )
                            ) ?: return@launch
                            prefStore.update { copy(color = newColor.toArgb()) }
                        }
                    },
                    modifier = Modifier.interactive(prefs.switch),
                    title = { Text("Color") },
                    subtitle = { Text("This is a color preference") }
                )
            }
            item {
                ListItem(
                    modifier = Modifier.interactive(prefs.switch),
                    title = { Text("Multi select list") },
                    subtitle = { Text("This is a multi select list preference") },
                    onClick = {
                        scope.launch {
                            val newItems = navigator.pushForResult(
                                MultiChoiceListKey(
                                    items = listOf(
                                        MultiChoiceListKey.Item("A", "A"),
                                        MultiChoiceListKey.Item("B", "B"),
                                        MultiChoiceListKey.Item("C", "C")
                                    ),
                                    selectedItems = prefs.multiChoice,
                                    title = "Multi select list"
                                )
                            ) ?: return@launch
                            prefStore.update { copy(multiChoice = newItems) }
                        }
                    }
                )
            }
            item {
                ListItem(
                    modifier = Modifier.interactive(prefs.switch),
                    title = { Text("Single item list") },
                    subtitle = { Text("This is a single item list preference") },
                    onClick = {
                        scope.launch {
                            val newItem = navigator.pushForResult(
                                SingleChoiceListKey(
                                    items = listOf(
                                        SingleChoiceListKey.Item("A", "A"),
                                        SingleChoiceListKey.Item("B", "B"),
                                        SingleChoiceListKey.Item("C", "C")
                                    ),
                                    selectedItem = prefs.singleChoice,
                                    title = "Single item list"
                                )
                            ) ?: return@launch
                            prefStore.update { copy(singleChoice = newItem) }
                        }
                    }
                )
            }
        }
    }
}

@Serializable
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

@Given
val samplePrefsModule = PrefStoreModule<SamplePrefs>("sample_prefs")
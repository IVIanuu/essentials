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

import androidx.compose.foundation.lazy.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.dialog.TextInputKey
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.prefs.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.serialization.*

@Given
val prefsHomeItem = HomeItem("Prefs") { PrefsKey }

object PrefsKey : Key<Nothing>

@Given
fun prefsUi(
    @Given navigator: Navigator,
    @Given prefStore: DataStore<SamplePrefs>,
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>
): KeyUi<PrefsKey> = {
    val prefs by prefStore.data.collectAsState(remember { SamplePrefs() })
    Scaffold(
        topBar = { TopAppBar(title = { Text("Prefs") }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                SwitchListItem(
                    value = prefs.switch,
                    onValueChange = {
                        scope.launch {
                            prefStore.updateData { copy(switch = it) }
                        }
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
                        scope.launch {
                            prefStore.updateData { copy(checkbox = it) }
                        }
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
                        scope.launch {
                            prefStore.updateData { copy(radioButton = it) }
                        }
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
                        scope.launch {
                            prefStore.updateData { copy(slider = it) }
                        }
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
                            scope.launch {
                                prefStore.updateData { copy(textInput = newTextInput) }
                            }
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
                                com.ivianuu.essentials.colorpicker.ColorPickerKey(
                                    initialColor = Color(prefs.color),
                                    title = "Color"
                                )
                            ) ?: return@launch
                            scope.launch {
                                prefStore.updateData { copy(color = newColor.toArgb()) }
                            }
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
                            scope.launch {
                                prefStore.updateData { copy(multiChoice = newItems) }
                            }
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
                            scope.launch {
                                prefStore.updateData { copy(singleChoice = newItem) }
                            }
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
val samplePrefsModule = PrefModule("sample_prefs") { SamplePrefs() }
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

import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.colorpicker.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.compose.*
import com.ivianuu.injekt.scope.*

@Given
val dialogsHomeItem = HomeItem("Dialogs") { DialogsKey }

object DialogsKey : Key<Nothing>

@Given
val dialogsUi: KeyUi<DialogsKey> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Dialogs") }) }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = localVerticalInsetsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                DialogLauncherButton(text = "Simple") {
                    Dialog(
                        title = { Text("Simple") },
                        content = { Text("This is a message") },
                        buttons = {
                            DialogCloseButton(text = "Cancel")
                            DialogCloseButton(text = "OK")
                        }
                    )
                }
            }
            item {
                DialogLauncherButton(
                    text = "Simple with neutral"
                ) {
                    Dialog(
                        title = { Text("Simple") },
                        content = { Text("This is a message") },
                        buttons = {
                            DialogCloseButton(text = "Neutral")
                            DialogCloseButton(text = "Negative")
                            DialogCloseButton(text = "Positive")
                        }
                    )
                }
            }
            item {
                DialogLauncherButton(text = "Title only") {
                    Dialog(
                        title = { Text("Title only") }
                    )
                }
            }
            item {
                DialogLauncherButton(text = "With icon") {
                    Dialog(
                        title = { Text("With icon") },
                        icon = { Icon(Icons.Default.Settings, null) },
                        buttons = {
                            DialogCloseButton(text = "OK")
                        }
                    )
                }
            }
            item {
                DialogLauncherButton(text = "Message only") {
                    Dialog(content = { Text("Message only") })
                }
            }
            item {
                DialogLauncherButton(text = "Buttons only") {
                    Dialog(
                        buttons = {
                            DialogCloseButton(text = "Cancel")
                            DialogCloseButton(text = "OK")
                        }
                    )
                }
            }
            item {
                DialogLauncherButton(
                    dismissible = false,
                    text = "Not cancelable"
                ) {
                    Dialog(
                        title = { Text("Not cancelable") },
                        buttons = {
                            DialogCloseButton(text = "Close")
                        }
                    )
                }
            }
            item {
                DialogLauncherButton(text = "List") {
                    Dialog(
                        title = { Text("List") },
                        content = {
                            LazyColumn {
                                items((1..100).toList()) { item ->
                                    ListItem(
                                        title = {
                                            Text(
                                                "Item: $item"
                                            )
                                        },
                                        onClick = {}
                                    )
                                }
                            }
                        },
                        applyContentPadding = false,
                        buttons = {
                            DialogCloseButton(text = "Close")
                        }
                    )
                }
            }
            item {
                val singleChoiceItems = listOf(1, 2, 3, 4, 5)
                var selectedSingleChoiceItem by remember { mutableStateOf(1) }
                DialogLauncherButton(text = "Single choice list") {
                    var tmpSelectedItem by remember { mutableStateOf(selectedSingleChoiceItem) }

                    SingleChoiceListDialog(
                        title = { Text("Single choice") },
                        items = singleChoiceItems,
                        selectedItem = tmpSelectedItem,
                        onSelectionChanged = { tmpSelectedItem = it },

                        item = { Text("Item: $it") },
                        buttons = {
                            DialogCloseButton(text = "Cancel")
                            DialogCloseButton(
                                text = "OK",
                                onClick = { selectedSingleChoiceItem = tmpSelectedItem }
                            )
                        }
                    )
                }
            }
            item {
                val multiChoiceItems = listOf("A", "B", "C")
                var selectedMultiChoiceItems by remember { mutableStateOf(multiChoiceItems.toSet()) }
                DialogLauncherButton(text = "Multi choice list") {
                    var tmpSelectedItems by remember { mutableStateOf(selectedMultiChoiceItems) }

                    MultiChoiceListDialog(
                        title = { Text("Multi choice") },
                        items = multiChoiceItems,
                        selectedItems = tmpSelectedItems,
                        onSelectionsChanged = { tmpSelectedItems = it },
                        item = { Text(it) },
                        buttons = {
                            DialogCloseButton(text = "Cancel")
                            DialogCloseButton(
                                text = "OK",
                                onClick = { selectedMultiChoiceItems = tmpSelectedItems }
                            )
                        }
                    )
                }
            }
            item {
                val primaryColor = MaterialTheme.colors.primary
                var currentColor by remember { mutableStateOf(primaryColor) }
                DialogLauncherButton(text = "Color Picker") { dismiss ->
                    ColorPickerDialog(
                        title = { Text("Color Picker") },
                        showAlphaSelector = true,
                        initialColor = currentColor,
                        onCancel = dismiss,
                        onColorSelected = {
                            currentColor = it
                            dismiss()
                        }
                    )
                }
            }
            item {
                var textInputValue by remember { mutableStateOf("") }
                DialogLauncherButton(text = "Text input") {
                    var tmpTextInputValue by remember { mutableStateOf(textInputValue) }
                    TextInputDialog(
                        value = tmpTextInputValue,
                        onValueChange = { tmpTextInputValue = it },
                        title = { Text("Text input") },
                        label = { Text("Hint..") },
                        buttons = {
                            DialogCloseButton(text = "Cancel")
                            DialogCloseButton(
                                text = "OK",
                                onClick = { textInputValue = tmpTextInputValue },
                                enabled = tmpTextInputValue.isNotEmpty()
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogCloseButton(
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    text: String
) {
    val component = rememberElement<DialogLauncherComponent>()
    TextButton(
        enabled = enabled,
        onClick = {
            onClick()
            component.navigator.popTop()
        }
    ) {
        Text(text)
    }
}

@Composable
private fun DialogLauncherButton(
    text: String,
    dismissible: Boolean = true,
    dialog: @Composable (() -> Unit) -> Unit
) {
    Spacer(Modifier.height(8.dp))

    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current!!
    val component = rememberElement<DialogLauncherComponent>()
    Button(
        onClick = {
            component.navigator.push(
                DialogLauncherKey(dismissible) {
                    dialog {
                        if (dismissible) {
                            onBackPressedDispatcherOwner.onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
            )
        }
    ) { Text(text) }
}

data class DialogLauncherKey(
    val dismissible: Boolean = true,
    val dialog: @Composable () -> Unit
) : DialogKey<Nothing>

@Given
fun dialogLauncherUi(@Given key: DialogLauncherKey): KeyUi<DialogLauncherKey> = {
    DialogScaffold(dismissible = key.dismissible) { key.dialog() }
}

@InstallElement<UiGivenScope>
@Given
class DialogLauncherComponent(@Given val navigator: Navigator)

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

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.ui.LocalUiGivenScope
import com.ivianuu.essentials.ui.UiGivenScope
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.dialog.AlertDialogButtonLayout
import com.ivianuu.essentials.ui.dialog.ColorPickerDialog
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKeyUiOptionsFactory
import com.ivianuu.essentials.ui.dialog.DialogWrapper
import com.ivianuu.essentials.ui.dialog.MultiChoiceListDialog
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.dialog.TextInputDialog
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.GivenScopeElementBinding

@Given
val dialogsHomeItem = HomeItem("Dialogs") { DialogsKey() }

class DialogsKey : Key<Nothing>

@Given
val dialogsKeyModule = KeyModule<DialogsKey>()

@Given
fun dialogsUi(): KeyUi<DialogsKey> = {
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
                DialogLauncherButton(
                    text = "Simple"
                ) {
                    Dialog(
                        title = { Text("Simple") },
                        content = { Text("This is a message") },
                        positiveButton = { DialogCloseButton(text = "OK") },
                        negativeButton = {
                            DialogCloseButton(text = "Cancel")
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
                        positiveButton = {
                            DialogCloseButton(text = "Positive")
                        },
                        negativeButton = {
                            DialogCloseButton(text = "Negative")
                        },
                        neutralButton = {
                            DialogCloseButton(text = "Neutral")
                        }
                    )
                }
            }
            item {
                DialogLauncherButton(
                    text = "Title only"
                ) {
                    Dialog(
                        title = { Text("Title only") }
                    )
                }
            }
            item {
                DialogLauncherButton(
                    text = "With icon"
                ) {
                    Dialog(
                        title = { Text("With icon") },
                        icon = { Icon(Icons.Default.Settings, null) },
                        positiveButton = {
                            DialogCloseButton(text = "OK")
                        }
                    )
                }
            }
            item {
                DialogLauncherButton(
                    text = "Message only"
                ) {
                    Dialog(
                        content = { Text("Message only") }
                    )
                }
            }
            item {
                DialogLauncherButton(
                    text = "Buttons only"
                ) {
                    Dialog(
                        positiveButton = {
                            DialogCloseButton(text = "OK")
                        },
                        negativeButton = {
                            DialogCloseButton(text = "Cancel")
                        }
                    )
                }
            }
            item {
                DialogLauncherButton(
                    text = "Stacked buttons"
                ) {
                    Dialog(
                        title = { Text("Stacked buttons") },
                        content = { Text("Shows stacked buttons") },
                        buttonLayout = AlertDialogButtonLayout.STACKED,
                        positiveButton = {
                            DialogCloseButton(text = "OK")
                        },
                        negativeButton = {
                            DialogCloseButton(text = "Cancel")
                        }
                    )
                }
            }
            item {
                DialogLauncherButton(
                    text = "Stacked buttons with neutral"
                ) {
                    Dialog(
                        title = { Text("Stacked buttons") },
                        content = { Text("Shows stacked buttons") },
                        buttonLayout = AlertDialogButtonLayout.STACKED,
                        positiveButton = {
                            DialogCloseButton(text = "Positive")
                        },
                        negativeButton = {
                            DialogCloseButton(text = "Negative")
                        },
                        neutralButton = {
                            DialogCloseButton(text = "Neutral")
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
                        negativeButton = {
                            DialogCloseButton(text = "Close")
                        }
                    )
                }
            }
            item {
                DialogLauncherButton(
                    text = "List"
                ) {
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
                        negativeButton = {
                            DialogCloseButton(text = "Close")
                        }
                    )
                }
            }
            item {
                val singleChoiceItems = listOf(1, 2, 3, 4, 5)
                var selectedSingleChoiceItem by remember { mutableStateOf(1) }
                DialogLauncherButton(
                    text = "Single choice list"
                ) {
                    var tmpSelectedItem by remember { mutableStateOf(selectedSingleChoiceItem) }

                    SingleChoiceListDialog(
                        title = { Text("Single choice") },
                        items = singleChoiceItems,
                        selectedItem = tmpSelectedItem,
                        onSelectionChanged = { tmpSelectedItem = it },

                        item = { Text("Item: $it") },
                        positiveButton = {
                            DialogCloseButton(
                                text = "OK",
                                onClick = { selectedSingleChoiceItem = tmpSelectedItem }
                            )
                        },
                        negativeButton = {
                            DialogCloseButton(text = "Cancel")
                        }
                    )
                }
            }
            item {
                val multiChoiceItems = listOf("A", "B", "C")
                var selectedMultiChoiceItems by remember { mutableStateOf(multiChoiceItems.toSet()) }
                DialogLauncherButton(
                    text = "Multi choice list"
                ) {
                    var tmpSelectedItems by remember { mutableStateOf(selectedMultiChoiceItems) }

                    MultiChoiceListDialog(
                        title = { Text("Multi choice") },
                        items = multiChoiceItems,
                        selectedItems = tmpSelectedItems,
                        onSelectionsChanged = { tmpSelectedItems = it },
                        item = { Text(it) },
                        positiveButton = {
                            DialogCloseButton(
                                text = "OK",
                                onClick = { selectedMultiChoiceItems = tmpSelectedItems }
                            )
                        },
                        negativeButton = {
                            DialogCloseButton(text = "Cancel")
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
                        positiveButton = {
                            DialogCloseButton(
                                text = "OK",
                                onClick = { textInputValue = tmpTextInputValue },
                                enabled = tmpTextInputValue.isNotEmpty()
                            )
                        },
                        negativeButton = {
                            DialogCloseButton(text = "Cancel")
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
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    TextButton(
        enabled = enabled,
        onClick = {
            onClick()
            onBackPressedDispatcherOwner.onBackPressedDispatcher.onBackPressed()
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

    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val component = LocalUiGivenScope.current.element<DialogLauncherComponent>()
    Button(
        onClick = {
            component.navigator.push(
                DialogLauncherKey {
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

data class DialogLauncherKey(val dialog: @Composable () -> Unit) : Key<Nothing>

@Given
val dialogLauncherKeyModule = KeyModule<DialogLauncherKey>()

@Given
fun dialogLauncherUi(@Given key: DialogLauncherKey): KeyUi<DialogLauncherKey> = {
    DialogWrapper { key.dialog() }
}

@Given
val dialogLauncherUiOptionsFactory = DialogKeyUiOptionsFactory<DialogLauncherKey>()

@GivenScopeElementBinding<UiGivenScope>
@Given
class DialogLauncherComponent(@Given val navigator: Navigator)

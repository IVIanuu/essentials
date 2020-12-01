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

import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.material.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.AmbientUiComponent
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.essentials.ui.core.rememberState
import com.ivianuu.essentials.ui.dialog.AlertDialogButtonLayout
import com.ivianuu.essentials.ui.dialog.ColorPickerDialog
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogWrapper
import com.ivianuu.essentials.ui.dialog.MultiChoiceListDialog
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.dialog.TextInputDialog
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent

class DialogsKey

@KeyUiBinding<DialogsKey>
@FunBinding
@Composable
fun DialogsPage() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Dialogs") }) }
    ) {
        InsettingScrollableColumn {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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


                DialogLauncherButton(
                    text = "Title only"
                ) {
                    Dialog(
                        title = { Text("Title only") }
                    )
                }

                DialogLauncherButton(
                    text = "With icon"
                ) {
                    Dialog(
                        title = { Text("With icon") },
                        icon = { Icon(Icons.Default.Settings) },
                        positiveButton = {
                            DialogCloseButton(text = "OK")
                        }
                    )
                }

                DialogLauncherButton(
                    text = "Message only"
                ) {
                    Dialog(
                        content = { Text("Message only") }
                    )
                }

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

                DialogLauncherButton(
                    text = "Stacked buttons"
                ) {
                    Dialog(
                        title = { Text("Stacked buttons") },
                        content = { Text("Shows stacked buttons") },
                        buttonLayout = AlertDialogButtonLayout.Stacked,
                        positiveButton = {
                            DialogCloseButton(text = "OK")
                        },
                        negativeButton = {
                            DialogCloseButton(text = "Cancel")
                        }
                    )
                }

                DialogLauncherButton(
                    text = "Stacked buttons with neutral"
                ) {
                    Dialog(
                        title = { Text("Stacked buttons") },
                        content = { Text("Shows stacked buttons") },
                        buttonLayout = AlertDialogButtonLayout.Stacked,
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

                DialogLauncherButton(
                    text = "List"
                ) {
                    Dialog(
                        title = { Text("List") },
                        content = {
                            ScrollableColumn {
                                (1..100).forEach {
                                    ListItem(
                                        title = {
                                            Text(
                                                "Item: $it"
                                            )
                                        },
                                        onClick = {

                                        }
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

                val singleChoiceItems = listOf(1, 2, 3, 4, 5)
                var selectedSingleChoiceItem by rememberState { 1 }
                DialogLauncherButton(
                    text = "Single choice list"
                ) {
                    var tmpSelectedItem by rememberState { selectedSingleChoiceItem }

                    SingleChoiceListDialog(
                        title = { Text("Single choice") },
                        items = singleChoiceItems,
                        selectedItem = tmpSelectedItem,
                        onSelect = { tmpSelectedItem = it },

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

                val multiChoiceItems = listOf("A", "B", "C")
                var selectedMultiChoiceItems by rememberState { multiChoiceItems }
                DialogLauncherButton(
                    text = "Multi choice list"
                ) {
                    var tmpSelectedItems by rememberState { selectedMultiChoiceItems }

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

                val primaryColor = MaterialTheme.colors.primary
                var currentColor by rememberState { primaryColor }
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

                var textInputValue by rememberState { "" }
                DialogLauncherButton(text = "Text input") {
                    var tmpTextInputValue by rememberState { textInputValue }
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
    val onBackPressedDispatcherOwner: OnBackPressedDispatcherOwner = compositionActivity
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

    val onBackPressedDispatcherOwner: OnBackPressedDispatcherOwner = compositionActivity
    val component = AmbientUiComponent.current
        .mergeComponent<DialogLauncherComponent>()
    Button(
        onClick = {
            component.dispatchNavigationOption(
                NavigationAction.Push(
                    dialog {
                        if (dismissible) {
                            onBackPressedDispatcherOwner.onBackPressedDispatcher.onBackPressed()
                        }
                    }
                )
            )
        }
    ) { Text(text) }
}

data class DialogLauncherKey(val dialog: @Composable () -> Unit)

@KeyUiBinding<DialogLauncherKey>
@FunBinding
@Composable
fun DialogLauncherDialog(key: DialogLauncherKey) {
    DialogWrapper { key.dialog() }
}

@MergeInto(UiComponent::class)
interface DialogLauncherComponent {
    val dispatchNavigationOption: DispatchAction<NavigationAction>
}

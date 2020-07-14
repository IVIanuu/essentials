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
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.height
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TextButton
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Settings
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.dialog.AlertDialogButtonLayout
import com.ivianuu.essentials.ui.dialog.ColorPickerDialog
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.MultiChoiceListDialog
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.dialog.TextInputDialog
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.DialogRoute
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.injekt.Reader
@Reader
@Composable
fun DialogsPage() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Dialogs") }) }
    ) {
        InsettingScrollableColumn {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalGravity = Alignment.CenterHorizontally
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
                            VerticalScroller {
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
                val (selectedSingleChoiceItem, setSelectedSingleChoiceItem) = state { 1 }
                DialogLauncherButton(
                    text = "Single choice list"
                ) {
                    val (tmpSelectedItem, setTmpSelectedItem) = state { selectedSingleChoiceItem }

                    SingleChoiceListDialog(
                        title = { Text("Single choice") },
                        items = singleChoiceItems,
                        selectedItem = tmpSelectedItem,
                        onSelect = setTmpSelectedItem,
                        dismissOnSelection = false,
                        item = { Text("Item: $it") },
                        positiveButton = {
                            DialogCloseButton(
                                text = "OK",
                                onClick = {
                                    setSelectedSingleChoiceItem(tmpSelectedItem)
                                })
                        },
                        negativeButton = {
                            DialogCloseButton(text = "Cancel")
                        }
                    )
                }

                val multiChoiceItems = listOf("A", "B", "C")
                val (selectedMultiChoiceItems, setSelectedMultiChoiceItems) = state { multiChoiceItems }
                DialogLauncherButton(
                    text = "Multi choice list"
                ) {
                    val (tmpSelectedItems, setTmpSelectedItems) = state { selectedMultiChoiceItems }

                    MultiChoiceListDialog(
                        title = { Text("Multi choice") },
                        items = multiChoiceItems,
                        selectedItems = tmpSelectedItems,
                        onSelectionsChanged = setTmpSelectedItems,
                        item = { Text(it) },
                        positiveButton = {
                            DialogCloseButton(
                                text = "OK",
                                onClick = {
                                    setSelectedMultiChoiceItems(tmpSelectedItems)
                                }
                            )
                        },
                        negativeButton = {
                            DialogCloseButton(text = "Cancel")
                        }
                    )
                }

                val primaryColor = MaterialTheme.colors.primary
                val (currentColor, setCurrentColor) = state { primaryColor }
                DialogLauncherButton(text = "Color Picker") { dismiss ->
                    ColorPickerDialog(
                        title = { Text("Color Picker") },
                        showAlphaSelector = true,
                        initialColor = currentColor,
                        onCancel = dismiss,
                        onColorSelected = {
                            setCurrentColor(it)
                            dismiss()
                        }
                    )
                }

                val (textInputValue, setTextInputValue) = state { "" }
                DialogLauncherButton(text = "Text input") {
                    val (tmpTextInputValue, setTmpTextInputValue) = state { textInputValue }
                    TextInputDialog(
                        title = { Text("Text input") },
                        value = tmpTextInputValue,
                        onValueChange = setTmpTextInputValue,
                        hint = "Hint..",
                        positiveButton = {
                            DialogCloseButton(
                                text = "OK",
                                onClick = {
                                    setTextInputValue(tmpTextInputValue)
                                },
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
    val navigator = NavigatorAmbient.current
    TextButton(
        enabled = enabled,
        onClick = {
            onClick()
            navigator.popTop()
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

    val navigator = NavigatorAmbient.current
    Button(
        onClick = {
            navigator.push(
                DialogRoute(dismissible = dismissible) {
                    dialog {
                        navigator.popTop()
                    }
                }
            )
        }
    ) { Text(text) }
}

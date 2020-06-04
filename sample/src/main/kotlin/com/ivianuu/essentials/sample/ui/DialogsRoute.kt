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
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Settings
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.AlertDialogButtonLayout
import com.ivianuu.essentials.ui.dialog.ColorPickerDialog
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogButton
import com.ivianuu.essentials.ui.dialog.DialogCloseButton
import com.ivianuu.essentials.ui.dialog.DialogRoute
import com.ivianuu.essentials.ui.dialog.MultiChoiceListDialog
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.dialog.TextInputDialog
import com.ivianuu.essentials.ui.dialog.VerticalScrollerDialog
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route

val DialogsRoute = Route {
    Scaffold(
        topAppBar = { TopAppBar(title = { Text("Dialogs") }) },
        body = {
            VerticalScroller {
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
                            positiveButton = {
                                DialogCloseButton { Text("OK") }
                            },
                            negativeButton = {
                                DialogCloseButton { Text("Cancel") }
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
                                DialogCloseButton { Text("Positive") }
                            },
                            negativeButton = {
                                DialogCloseButton { Text("Negative") }
                            },
                            neutralButton = {
                                DialogCloseButton { Text("Neutral") }
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
                                DialogCloseButton { Text("OK") }
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
                                DialogCloseButton { Text("OK") }
                            },
                            negativeButton = {
                                DialogCloseButton { Text("Cancel") }
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
                                DialogCloseButton { Text("OK") }
                            },
                            negativeButton = {
                                DialogCloseButton { Text("Cancel") }
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
                                DialogCloseButton { Text("Positive") }
                            },
                            negativeButton = {
                                DialogCloseButton { Text("Negative") }
                            },
                            neutralButton = {
                                DialogCloseButton { Text("Neutral") }
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
                                DialogCloseButton { Text("Close") }
                            }
                        )
                    }

                    DialogLauncherButton(
                        text = "List"
                    ) {
                        VerticalScrollerDialog(
                            title = { Text("List") },
                            scrollerContent = {
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
                            },
                            negativeButton = {
                                DialogCloseButton { Text("Close") }
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
                            itemCallback = { Text("Item: $it") },
                            positiveButton = {
                                DialogButton(
                                    onClick = {
                                        setSelectedSingleChoiceItem(tmpSelectedItem)
                                    }) { Text("OK") }
                            },
                            negativeButton = {
                                DialogCloseButton { Text("Cancel") }
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
                            itemCallback = { Text(it) },
                            positiveButton = {
                                DialogButton(
                                    onClick = {
                                        setSelectedMultiChoiceItems(tmpSelectedItems)
                                    }) { Text("OK") }
                            },
                            negativeButton = {
                                DialogCloseButton { Text("Cancel") }
                            }
                        )
                    }

                    val primaryColor = MaterialTheme.colors.primary
                    val (currentColor, setCurrentColor) = state { primaryColor }
                    DialogLauncherButton(text = "Color Picker") {
                        ColorPickerDialog(
                            title = { Text("Color Picker") },
                            showAlphaSelector = true,
                            initialColor = currentColor,
                            onColorSelected = setCurrentColor
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
                                DialogButton(
                                    enabled = tmpTextInputValue.isNotEmpty(),
                                    onClick = {
                                        setTextInputValue(tmpTextInputValue)
                                    }) { Text("OK") }
                            },
                            negativeButton = {
                                DialogCloseButton { Text("Cancel") }
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun DialogLauncherButton(
    text: String,
    dismissible: Boolean = true,
    dialog: @Composable () -> Unit
) {
    val navigator = NavigatorAmbient.current
    Button(
        onClick = {
            navigator.push(DialogRoute(dismissible = dismissible, dialog = dialog))
        }
    ) { Text(text) }

    Spacer(Modifier.preferredHeight(8.dp))
}
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
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Spacer
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.gestures.action.actions.Settings
import com.ivianuu.essentials.icon.Essentials
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.AlertDialogButtonLayout
import com.ivianuu.essentials.ui.dialog.ColorPickerDialog
import com.ivianuu.essentials.ui.dialog.DialogButton
import com.ivianuu.essentials.ui.dialog.DialogCloseButton
import com.ivianuu.essentials.ui.dialog.DialogRoute
import com.ivianuu.essentials.ui.dialog.MaterialDialog
import com.ivianuu.essentials.ui.dialog.MultiChoiceListDialog
import com.ivianuu.essentials.ui.dialog.ScrollableDialog
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.dialog.TextInputDialog
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.painter.DrawRenderable
import com.ivianuu.essentials.ui.painter.VectorRenderable

val DialogsRoute = Route {
    Scaffold(
        topAppBar = { TopAppBar("Dialogs") },
        body = {
            VerticalScroller {
                Column(
                    modifier = LayoutWidth.Fill + LayoutPadding(all = 8.dp),
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    DialogLauncherButton(
                        text = "Simple"
                    ) {
                        MaterialDialog(
                            title = { Text("Simple") },
                            content = { Text("This is a message") },
                            positiveButton = {
                                DialogCloseButton(
                                    "OK"
                                )
                            },
                            negativeButton = {
                                DialogCloseButton(
                                    "Cancel"
                                )
                            }
                        )
                    }

                    DialogLauncherButton(
                        text = "Simple with neutral"
                    ) {
                        MaterialDialog(
                            title = { Text("Simple") },
                            content = { Text("This is a message") },
                            positiveButton = {
                                DialogCloseButton(
                                    "Positive"
                                )
                            },
                            negativeButton = {
                                DialogCloseButton(
                                    "Negative"
                                )
                            },
                            neutralButton = {
                                DialogCloseButton(
                                    "Neutral"
                                )
                            }
                        )
                    }


                    DialogLauncherButton(
                        text = "Title only"
                    ) {
                        MaterialDialog(
                            title = { Text("Title only") }
                        )
                    }

                    DialogLauncherButton(
                        text = "With icon"
                    ) {
                        MaterialDialog(
                            title = { Text("With icon") },
                            icon = {
                                DrawRenderable(
                                    VectorRenderable(Icons.Essentials.Settings)
                                )
                            },
                            positiveButton = {
                                DialogCloseButton(
                                    "OK"
                                )
                            }
                        )
                    }

                    DialogLauncherButton(
                        text = "Message only"
                    ) {
                        MaterialDialog(
                            content = { Text("Message only") }
                        )
                    }

                    DialogLauncherButton(
                        text = "Buttons only"
                    ) {
                        MaterialDialog(
                            positiveButton = {
                                DialogCloseButton(
                                    "OK"
                                )
                            },
                            negativeButton = {
                                DialogCloseButton(
                                    "Cancel"
                                )
                            }
                        )
                    }

                    DialogLauncherButton(
                        text = "Stacked buttons"
                    ) {
                        MaterialDialog(
                            title = { Text("Stacked buttons") },
                            content = { Text("Shows stacked buttons") },
                            buttonLayout = AlertDialogButtonLayout.Stacked,
                            positiveButton = {
                                DialogCloseButton(
                                    "OK"
                                )
                            },
                            negativeButton = {
                                DialogCloseButton(
                                    "Cancel"
                                )
                            }
                        )
                    }

                    DialogLauncherButton(
                        text = "Stacked buttons with neutral"
                    ) {
                        MaterialDialog(
                            title = { Text("Stacked buttons") },
                            content = { Text("Shows stacked buttons") },
                            buttonLayout = AlertDialogButtonLayout.Stacked,
                            positiveButton = {
                                DialogCloseButton(
                                    "Positive"
                                )
                            },
                            negativeButton = {
                                DialogCloseButton(
                                    "Negative"
                                )
                            },
                            neutralButton = {
                                DialogCloseButton(
                                    "Neutral"
                                )
                            }
                        )
                    }

                    DialogLauncherButton(
                        dismissible = false,
                        text = "Not cancelable"
                    ) {
                        MaterialDialog(
                            title = { Text("Not cancelable") },
                            negativeButton = {
                                DialogCloseButton(
                                    "Close"
                                )
                            }
                        )
                    }

                    DialogLauncherButton(
                        text = "List"
                    ) {
                        ScrollableDialog(
                            title = { Text("List") },
                            listContent = {
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
                                DialogCloseButton(
                                    "Close"
                                )
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
                            itemCallback = { Text(it) },
                            positiveButton = {
                                DialogButton(
                                    text = "OK",
                                    onClick = {
                                        setSelectedMultiChoiceItems(tmpSelectedItems)
                                    })
                            },
                            negativeButton = {
                                DialogCloseButton(text = "Cancel")
                            }
                        )
                    }

                    val primaryColor = MaterialTheme.colors().primary
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
                                    text = "OK",
                                    onClick = {
                                        setTextInputValue(tmpTextInputValue)
                                    })
                            },
                            negativeButton = {
                                DialogCloseButton(
                                    text = "Cancel"
                                )
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
        text = text,
        onClick = {
            navigator.push(DialogRoute(dismissible = dismissible, dialog = dialog))
        }
    )

    Spacer(LayoutHeight(8.dp))
}
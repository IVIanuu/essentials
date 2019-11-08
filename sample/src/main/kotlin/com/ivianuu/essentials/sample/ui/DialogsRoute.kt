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
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.material.Button
import androidx.ui.material.themeColor
import com.ivianuu.essentials.picker.ColorPickerDialog
import com.ivianuu.essentials.picker.TextInputDialog
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.dialog.AlertDialogButtonLayout
import com.ivianuu.essentials.ui.compose.dialog.DialogButton
import com.ivianuu.essentials.ui.compose.dialog.DialogCloseButton
import com.ivianuu.essentials.ui.compose.dialog.ListDialog
import com.ivianuu.essentials.ui.compose.dialog.MaterialDialog
import com.ivianuu.essentials.ui.compose.dialog.MultiChoiceListDialog
import com.ivianuu.essentials.ui.compose.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.compose.dialog.composeDialogRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Icon
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.compose.resources.drawableResource
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade

val dialogsRoute = composeControllerRoute(
    options = controllerRouteOptions().fade()
) {
    Scaffold(
        topAppBar = { EsTopAppBar("Dialogs") },
        body = {
            VerticalScroller {
                Column(
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
                            icon = { Icon(+drawableResource(R.drawable.ic_settings)) },
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
                        ListDialog(
                            title = { Text("List") },
                            listContent = {
                                (1..100).forEach {
                                    SimpleListItem(
                                        title = { Text("Item: $it") },
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
                    val (selectedSingleChoiceItem, setSelectedSingleChoiceItem) = +state { 1 }
                    DialogLauncherButton(
                        text = "Single choice list"
                    ) {
                        val (tmpSelectedItem, setTmpSelectedItem) = +state { selectedSingleChoiceItem }

                        SingleChoiceListDialog(
                            title = { Text("Single choice") },
                            items = singleChoiceItems,
                            selectedItem = tmpSelectedItem,
                            onSelect = setTmpSelectedItem,
                            dismissOnSelect = false,
                            item = { Text("Item: $it") },
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
                    val (selectedMultiChoiceItems, setSelectedMultiChoiceItems) = +state { multiChoiceItems }
                    DialogLauncherButton(
                        text = "Multi choice list"
                    ) {
                        val (tmpSelectedItems, setTmpSelectedItems) = +state { selectedMultiChoiceItems }

                        MultiChoiceListDialog(
                            title = { Text("Multi choice") },
                            items = multiChoiceItems,
                            selectedItems = tmpSelectedItems,
                            onSelectionsChanged = setTmpSelectedItems,
                            item = { Text(it) },
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

                    val primaryColor = +themeColor { primary }
                    val (currentColor, setCurrentColor) = +state { primaryColor }
                    DialogLauncherButton(text = "Color Picker") {
                        ColorPickerDialog(
                            title = { Text("Color Picker") },
                            showAlphaSelector = true,
                            initialColor = currentColor,
                            onColorSelected = setCurrentColor
                        )
                    }

                    val (textInputValue, setTextInputValue) = +state { "" }
                    DialogLauncherButton(text = "Text input") {
                        val (tmpTextInputValue, setTmpTextInputValue) = +state { textInputValue }
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
    dialog: @Composable() () -> Unit
) = composable("DialogLauncherButton") {
    val navigator = +inject<Navigator>()
    Button(
        text = text,
        onClick = {
            navigator.push(composeDialogRoute(dismissible = dismissible, dialog = dialog))
        }
    )

    HeightSpacer(8.dp)
}
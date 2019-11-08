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
import androidx.compose.ambient
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.material.Button
import androidx.ui.material.themeColor
import com.ivianuu.essentials.picker.ColorPickerDialog
import com.ivianuu.essentials.picker.PRIMARY_COLORS
import com.ivianuu.essentials.picker.PRIMARY_COLORS_SUB
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.dialog.DialogManagerAmbient
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Icon
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.compose.material.dialog.AlertDialog
import com.ivianuu.essentials.ui.compose.material.dialog.DialogButton
import com.ivianuu.essentials.ui.compose.material.dialog.DialogCloseButton
import com.ivianuu.essentials.ui.compose.material.dialog.ListDialog
import com.ivianuu.essentials.ui.compose.material.dialog.MultiChoiceListDialog
import com.ivianuu.essentials.ui.compose.material.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.compose.resources.drawableResource
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
                        AlertDialog(
                            title = { Text("Simple") },
                            content = { Text("This is a message") },
                            positiveButton = { DialogCloseButton("OK") },
                            negativeButton = { DialogCloseButton("Cancel") }
                        )
                    }


                    DialogLauncherButton(
                        text = "Title only"
                    ) {
                        AlertDialog(
                            title = { Text("Title only") }
                        )
                    }

                    DialogLauncherButton(
                        text = "With icon"
                    ) {
                        AlertDialog(
                            title = { Text("With icon") },
                            icon = { Icon(+drawableResource(R.drawable.ic_settings)) },
                            positiveButton = { DialogCloseButton("OK") }
                        )
                    }

                    DialogLauncherButton(
                        text = "Message only"
                    ) {
                        AlertDialog(
                            content = { Text("Message only") }
                        )
                    }

                    DialogLauncherButton(
                        text = "Buttons only"
                    ) {
                        AlertDialog(
                            positiveButton = { DialogCloseButton("OK") },
                            negativeButton = { DialogCloseButton("Cancel") }
                        )
                    }

                    DialogLauncherButton(
                        text = "Not cancelable"
                    ) {
                        AlertDialog(
                            dismissOnOutsideTouch = false,
                            title = { Text("Not cancelable") },
                            negativeButton = { DialogCloseButton("Close") }
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
                            negativeButton = { DialogCloseButton("Close") }
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
                                DialogButton(text = "OK", onClick = {
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
                                DialogButton(text = "OK", onClick = {
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
                            colors = PRIMARY_COLORS.map { Color(it) }.toList() + PRIMARY_COLORS_SUB.flatMap { it.toList() }.map {
                                Color(
                                    it
                                )
                            },
                            initialColor = currentColor,
                            onColorSelected = setCurrentColor
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
    dialog: @Composable() () -> Unit
) = composable("DialogLauncherButton") {
    val dialogManager = +ambient(DialogManagerAmbient)
    Button(
        text = text,
        onClick = {
            dialogManager.showDialog { dialog() }
        }
    )

    HeightSpacer(8.dp)
}
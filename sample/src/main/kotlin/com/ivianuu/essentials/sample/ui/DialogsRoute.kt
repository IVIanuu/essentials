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
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.material.Button
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.dialog.DialogManagerAmbient
import com.ivianuu.essentials.ui.compose.dialog.DismissDialogAmbient
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Icon
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.compose.material.dialog.AlertDialog
import com.ivianuu.essentials.ui.compose.material.dialog.DialogButton
import com.ivianuu.essentials.ui.compose.material.dialog.DialogCloseButton
import com.ivianuu.essentials.ui.compose.material.dialog.ListDialog
import com.ivianuu.essentials.ui.compose.material.dialog.SingleChoiceDialogListItem
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
                    crossAxisAlignment = CrossAxisAlignment.Start
                ) {
                    DialogLauncherButton(
                        text = "Simple"
                    ) {
                        AlertDialog(
                            title = { Text("Simple") },
                            content = { Text("This is a message") },
                            buttons = {
                                DialogButton(
                                    text = "OK",
                                    onClick = {})
                                DialogButton(
                                    text = "Cancel",
                                    onClick = {})
                            }
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
                            buttons = { DialogCloseButton("OK") }
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
                            buttons = {
                                DialogButton(
                                    text = "OK",
                                    onClick = {})

                                DialogButton(
                                    text = "Cancel",
                                    onClick = {})
                            }
                        )
                    }

                    DialogLauncherButton(
                        text = "Not cancelable"
                    ) {
                        AlertDialog(
                            dismissOnOutsideTouch = false,
                            title = { Text("Not cancelable") },
                            buttons = {
                                DialogButton(
                                    text = "Close",
                                    onClick = {})
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
                            buttons = {
                                DialogButton(
                                    text = "Close",
                                    onClick = {})
                            }
                        )
                    }

                    DialogLauncherButton(
                        text = "Single choice list"
                    ) {
                        val items = listOf(1, 2, 3, 4, 5)
                        val selectedItem = +state { 1 }
                        val dismissDialog = +ambient(DismissDialogAmbient)

                        SingleChoiceListDialog(
                            title = { Text("List") },
                            items = items,
                            selectedIndex = items.indexOf(selectedItem.value),
                            item = { _, selected, item ->
                                SingleChoiceDialogListItem(
                                    title = { Text("Item: $item") },
                                    selected = selected,
                                    onSelect = {
                                        selectedItem.value = item
                                        dismissDialog()
                                    }
                                )
                            },
                            buttons = {
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
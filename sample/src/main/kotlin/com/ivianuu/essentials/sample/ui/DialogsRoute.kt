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
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.ExpandedWidth
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Row
import androidx.ui.layout.WidthSpacer
import androidx.ui.material.Button
import androidx.ui.material.RadioButton
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.common.BlockChildTouches
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.dialog.DialogManagerAmbient
import com.ivianuu.essentials.ui.compose.dialog.DismissDialogAmbient
import com.ivianuu.essentials.ui.compose.material.AlertDialog
import com.ivianuu.essentials.ui.compose.material.DialogButton
import com.ivianuu.essentials.ui.compose.material.DialogCloseButton
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
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
                                DialogButton(text = "OK", onClick = {})
                                DialogButton(text = "Cancel", onClick = {})
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
                                DialogButton(text = "OK", onClick = {})

                                DialogButton(text = "Cancel", onClick = {})
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
                                DialogButton(text = "Close", onClick = {})
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
                                DialogButton(text = "Close", onClick = {})
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
                                SingleChoiceListDialogItem(
                                    title = "Item: $item",
                                    selected = selected,
                                    onSelect = {
                                        selectedItem.value = item
                                        dismissDialog()
                                    }
                                )
                            },
                            buttons = {
                                DialogCloseButton(text = "Cancel")
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun <T> SingleChoiceListDialog(
    dismissOnOutsideTouch: Boolean = true,
    dismissOnBackClick: Boolean = true,
    title: (@Composable() () -> Unit)? = null,
    buttons: (@Composable() () -> Unit)? = null,
    items: List<T>,
    selectedIndex: Int,
    item: @Composable() (Int, Boolean, T) -> Unit
) = composable("SingleChoiceListDialog") {
    ListDialog(
        dismissOnOutsideTouch = dismissOnOutsideTouch,
        dismissOnBackClick = dismissOnBackClick,
        applyContentPadding = false,
        title = title,
        buttons = buttons,
        listContent = {
            items.forEachIndexed { index, item ->
                item(index, index == selectedIndex, item)
            }
        }
    )
}

@Composable
fun SingleChoiceListDialogItem(
    title: String,
    selected: Boolean,
    onSelect: () -> Unit
) = composable("SingleChoiceListDialogItem") {
    Ripple(bounded = true) {
        Clickable(onClick = onSelect) {
            Container(
                modifier = ExpandedWidth,
                height = 48.dp,
                padding = EdgeInsets(
                    left = 24.dp,
                    right = 24.dp
                ),
                alignment = Alignment.CenterLeft
            ) {
                Row(
                    mainAxisAlignment = MainAxisAlignment.End,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    BlockChildTouches {
                        RadioButton(
                            selected = selected,
                            onSelect = {}
                        )
                    }

                    WidthSpacer(24.dp)

                    Text(
                        text = title,
                        style = +themeTextStyle { subtitle1 }
                    )
                }
            }
        }
    }
}

@Composable
fun ListDialog(
    dismissOnOutsideTouch: Boolean = true,
    dismissOnBackClick: Boolean = true,
    applyContentPadding: Boolean = true,
    title: (@Composable() () -> Unit)? = null,
    listContent: @Composable() () -> Unit,
    buttons: (@Composable() () -> Unit)? = null
) = composable("ListDialog") {
    AlertDialog(
        dismissOnOutsideTouch = dismissOnOutsideTouch,
        dismissOnBackClick = dismissOnBackClick,
        title = title,
        showDividers = true,
        applyContentPadding = applyContentPadding,
        content = {
            Container(
                modifier = ExpandedWidth,
                alignment = Alignment.TopLeft
            ) {
                VerticalScroller {
                    Column {
                        listContent()
                    }
                }
            }
        },
        buttons = buttons
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
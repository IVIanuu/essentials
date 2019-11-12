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

import androidx.compose.memo
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.surface.Surface
import androidx.ui.material.themeColor
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.common.AbsorbPointer
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollableList
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.compose.dialog.dialogRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsCheckbox
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.compose.material.Subheader
import com.ivianuu.essentials.ui.navigation.Navigator

val scaffoldRoute = composeControllerRoute {
    val navigator = +inject<Navigator>()

    val (showTopBar, setShowTopBar) = +state { true }
    val (centerTitle, setCenterTitle) = +state { false }

    val (bodyLayoutMode, setBodyLayout) = +state { Scaffold.BodyLayoutMode.Wrap }

    val (showBottomBar, setShowBottomBar) = +state { false }

    val (showFab, setShowFab) = +state { false }
    val (fabPosition, setFabPosition) = +state { Scaffold.FabPosition.End }

    Scaffold(
        topAppBar = if (showTopBar) ({
            composable("top bar") {
                val alpha = +memo(bodyLayoutMode) {
                    if (bodyLayoutMode == Scaffold.BodyLayoutMode.ExtendTop
                        || bodyLayoutMode == Scaffold.BodyLayoutMode.ExtendBoth
                    ) 0.5f else 1f
                }

                val color = (+themeColor { primary }).copy(alpha = alpha)

                EsTopAppBar(title = "Scaffold", color = color)
            }
        }) else null,
        fabPosition = fabPosition,
        fab = if (showFab) ({
            FloatingActionButton("Click me")
        }) else null,
        bottomBar = if (showBottomBar) ({
            composable("bottom bar") {
                val alpha = +memo(bodyLayoutMode) {
                    if (bodyLayoutMode == Scaffold.BodyLayoutMode.ExtendBottom
                        || bodyLayoutMode == Scaffold.BodyLayoutMode.ExtendBoth
                    ) 0.5f else 1f
                }

                val color = (+themeColor { primary }).copy(alpha = alpha)

                Surface(color = color) {
                    Container(
                        height = 56.dp,
                        expanded = true,
                        alignment = Alignment.CenterLeft,
                        padding = EdgeInsets(16.dp)
                    ) {
                        Text(
                            text = "Bottom bar",
                            style = +themeTextStyle { h6 }
                        )
                    }
                }
            }
        }) else null,
        bodyLayoutMode = bodyLayoutMode,
        body = {
            ScrollableList {
                Subheader("Top bar")
                SimpleListItem(
                    title = { Text("Show top bar") },
                    trailing = {
                        AbsorbPointer {
                            EsCheckbox(checked = showTopBar, onCheckedChange = {})
                        }
                    },
                    onClick = { setShowTopBar(!showTopBar) }
                )
                SimpleListItem(
                    title = { Text("Center title") },
                    trailing = {
                        AbsorbPointer {
                            EsCheckbox(checked = centerTitle, onCheckedChange = {})
                        }
                    },
                    onClick = { setCenterTitle(!centerTitle) }
                )

                Subheader("Body")
                SimpleListItem(
                    title = { Text("Body layout mode") },
                    onClick = {
                        navigator.push(
                            dialogRoute {
                                SingleChoiceListDialog(
                                    items = Scaffold.BodyLayoutMode.values().toList(),
                                    selectedItem = bodyLayoutMode,
                                    onSelect = {
                                        setBodyLayout(it)
                                        navigator.pop()
                                    },
                                    item = { Text(it.name) }
                                )
                            }
                        )
                    }
                )

                Subheader("Bottom bar")
                SimpleListItem(
                    title = { Text("Show bottom bar") },
                    trailing = {
                        AbsorbPointer {
                            EsCheckbox(checked = showBottomBar, onCheckedChange = {})
                        }
                    },
                    onClick = { setShowBottomBar(!showBottomBar) }
                )

                Subheader("Fab")
                SimpleListItem(
                    title = { Text("Show fab") },
                    trailing = {
                        AbsorbPointer {
                            EsCheckbox(checked = showFab, onCheckedChange = {})
                        }
                    },
                    onClick = { setShowFab(!showFab) }
                )
                SimpleListItem(
                    title = { Text("Fab location") },
                    onClick = if (showFab) ({
                        navigator.push(
                            dialogRoute {
                                SingleChoiceListDialog(
                                    items = Scaffold.FabPosition.values().toList(),
                                    selectedItem = fabPosition,
                                    onSelect = {
                                        setFabPosition(it)
                                        navigator.pop()
                                    },
                                    item = { Text(it.name) }
                                )
                            }
                        )
                    }) else null
                )
            }
        }
    )
}
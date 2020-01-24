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

import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.common.AbsorbPointer
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.common.framed
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.DialogRoute
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.Scroller
import com.ivianuu.essentials.ui.material.Checkbox
import com.ivianuu.essentials.ui.material.FloatingActionButton
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.ScaffoldState
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.Surface
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.material.TopAppBarStyle
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route

val ScaffoldRoute = Route {
    val navigator = NavigatorAmbient.current
    val controls = remember { ScaffoldControls() }

    Scaffold(
        topAppBar = if (controls.showTopAppBar) ({
            val alpha = remember(controls.bodyLayoutMode) {
                if (controls.bodyLayoutMode == ScaffoldState.BodyLayoutMode.ExtendTop
                    || controls.bodyLayoutMode == ScaffoldState.BodyLayoutMode.ExtendBoth
                ) 0.5f else 1f
            }

            val color = MaterialTheme.colors().primary.copy(alpha = alpha)

            TopAppBar(
                title = { Text("Scaffold") },
                style = TopAppBarStyle(
                    color = color,
                    elevation = if (alpha == 1f) 8.dp else 0.dp
                )
            )
        }) else null,
        fabPosition = controls.fabPosition,
        fab = if (controls.showFab) ({
            FloatingActionButton("Click me", onClick = {})
        }) else null,
        bottomBar = if (controls.showBottomBar) ({
            val alpha = remember(controls.bodyLayoutMode) {
                if (controls.bodyLayoutMode == ScaffoldState.BodyLayoutMode.ExtendBottom
                    || controls.bodyLayoutMode == ScaffoldState.BodyLayoutMode.ExtendBoth
                ) 0.5f else 1f
            }

            val color = MaterialTheme.colors().primary.copy(alpha = alpha)

            Surface(color = color) {
                SafeArea(
                    top = false,
                    left = false,
                    right = false,
                    bottom = true
                ) {
                    Container(
                        height = 56.dp,
                        expanded = true,
                        alignment = Alignment.CenterLeft,
                        padding = EdgeInsets(16.dp)
                    ) {
                        Text(
                            text = "Bottom bar",
                            style = MaterialTheme.typography().h6
                        )
                    }
                }
            }
        }) else null,
        bodyLayoutMode = controls.bodyLayoutMode,
        body = {
            Scroller {
                Column {
                    Subheader("Top bar")
                    ListItem(
                        title = { Text("Show top bar") },
                        trailing = {
                            AbsorbPointer {
                                Checkbox(checked = controls.showTopAppBar, onCheckedChange = {})
                            }
                        },
                        onClick = { controls.showTopAppBar = !controls.showTopAppBar }
                    )
                    ListItem(
                        title = { Text("Center title") },
                        trailing = {
                            AbsorbPointer {
                                Checkbox(checked = controls.centerTitle, onCheckedChange = {})
                            }
                        },
                        onClick = { controls.centerTitle = !controls.centerTitle }
                    )

                    Subheader("Body")
                    ListItem(
                        title = { Text("Body layout mode") },
                        onClick = {
                            navigator.push(
                                DialogRoute {
                                    SingleChoiceListDialog(
                                        items = ScaffoldState.BodyLayoutMode.values().toList(),
                                        selectedItem = controls.bodyLayoutMode,
                                        onSelect = { controls.bodyLayoutMode = it },
                                        item = {
                                            Text(
                                                it.name
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    )

                    Subheader("Bottom bar")
                    ListItem(
                        title = { Text("Show bottom bar") },
                        trailing = {
                            AbsorbPointer {
                                Checkbox(checked = controls.showBottomBar, onCheckedChange = {})
                            }
                        },
                        onClick = { controls.showBottomBar = !controls.showBottomBar }
                    )

                    Subheader("Fab")
                    ListItem(
                        title = { Text("Show fab") },
                        trailing = {
                            AbsorbPointer {
                                Checkbox(checked = controls.showFab, onCheckedChange = {})
                            }
                        },
                        onClick = { controls.showFab = !controls.showFab }
                    )
                    ListItem(
                        title = { Text("Fab location") },
                        onClick = {
                            navigator.push(
                                DialogRoute {
                                    SingleChoiceListDialog(
                                        items = ScaffoldState.FabPosition.values().toList(),
                                        selectedItem = controls.fabPosition,
                                        onSelect = { controls.fabPosition = it },
                                        item = {
                                            Text(
                                                it.name
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
    )
}

private class ScaffoldControls {
    var showTopAppBar by framed(true)
    var centerTitle by framed(false)
    var bodyLayoutMode by framed(ScaffoldState.BodyLayoutMode.Wrap)
    var showBottomBar by framed(false)
    var showFab by framed(false)
    var fabPosition by framed(ScaffoldState.FabPosition.End)
}
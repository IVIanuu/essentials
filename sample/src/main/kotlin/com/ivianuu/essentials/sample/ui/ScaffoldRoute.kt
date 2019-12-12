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

import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Surface
import com.ivianuu.essentials.ui.compose.common.AbsorbPointer
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollableList
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.core.remember
import com.ivianuu.essentials.ui.compose.dialog.DialogRoute
import com.ivianuu.essentials.ui.compose.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.compose.es.ComposeControllerRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsCheckbox
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.compose.material.Subheader
import com.ivianuu.essentials.ui.navigation.Navigator

val ScaffoldRoute = ComposeControllerRoute {
    val navigator = inject<Navigator>()
    val controls = remember { ScaffoldControls() }

    Scaffold(
        topAppBar = if (controls.showTopAppBar) ({
            val alpha = remember(controls.bodyLayoutMode) {
                if (controls.bodyLayoutMode == Scaffold.BodyLayoutMode.ExtendTop
                    || controls.bodyLayoutMode == Scaffold.BodyLayoutMode.ExtendBoth
                ) 0.5f else 1f
            }

            val color = MaterialTheme.colors()().primary.copy(alpha = alpha)

            EsTopAppBar(title = { Text("Scaffold") }, color = color)
        }) else null,
        fabPosition = controls.fabPosition,
        fab = if (controls.showFab) ({
            FloatingActionButton("Click me")
        }) else null,
        bottomBar = if (controls.showBottomBar) ({
            val alpha = remember(controls.bodyLayoutMode) {
                if (controls.bodyLayoutMode == Scaffold.BodyLayoutMode.ExtendBottom
                    || controls.bodyLayoutMode == Scaffold.BodyLayoutMode.ExtendBoth
                ) 0.5f else 1f
            }

            val color = MaterialTheme.colors()().primary.copy(alpha = alpha)

            Surface(color = color) {
                Container(
                    height = 56.dp,
                    expanded = true,
                    alignment = Alignment.CenterLeft,
                    padding = EdgeInsets(16.dp)
                ) {
                    Text(
                        text = "Bottom bar",
                        style = MaterialTheme.typography()().h6
                    )
                }
            }
        }) else null,
        bodyLayoutMode = controls.bodyLayoutMode,
        body = {
            ScrollableList {
                Subheader("Top bar")
                SimpleListItem(
                    title = { Text("Show top bar") },
                    trailing = {
                        AbsorbPointer {
                            EsCheckbox(checked = controls.showTopAppBar, onCheckedChange = {})
                        }
                    },
                    onClick = { controls.showTopAppBar = !controls.showTopAppBar }
                )
                SimpleListItem(
                    title = { Text("Center title") },
                    trailing = {
                        AbsorbPointer {
                            EsCheckbox(checked = controls.centerTitle, onCheckedChange = {})
                        }
                    },
                    onClick = { controls.centerTitle = !controls.centerTitle }
                )

                Subheader("Body")
                SimpleListItem(
                    title = { Text("Body layout mode") },
                    onClick = {
                        navigator.push(
                            DialogRoute {
                                SingleChoiceListDialog(
                                    items = Scaffold.BodyLayoutMode.values().toList(),
                                    selectedItem = controls.bodyLayoutMode,
                                    onSelect = {
                                        controls.bodyLayoutMode = it
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
                            EsCheckbox(checked = controls.showBottomBar, onCheckedChange = {})
                        }
                    },
                    onClick = { controls.showBottomBar = !controls.showBottomBar }
                )

                Subheader("Fab")
                SimpleListItem(
                    title = { Text("Show fab") },
                    trailing = {
                        AbsorbPointer {
                            EsCheckbox(checked = controls.showFab, onCheckedChange = {})
                        }
                    },
                    onClick = { controls.showFab = !controls.showFab }
                )
                SimpleListItem(
                    title = { Text("Fab location") },
                    onClick = {
                        navigator.push(
                            DialogRoute {
                                SingleChoiceListDialog(
                                    items = Scaffold.FabPosition.values().toList(),
                                    selectedItem = controls.fabPosition,
                                    onSelect = {
                                        controls.fabPosition = it
                                        navigator.pop()
                                    },
                                    item = { Text(it.name) }
                                )
                            }
                        )
                    }
                )
            }
        }
    )
}

private class ScaffoldControls {
    var showTopAppBar by framed(true)
    var centerTitle by framed(false)
    var bodyLayoutMode by framed(Scaffold.BodyLayoutMode.Wrap)
    var showBottomBar by framed(false)
    var showFab by framed(false)
    var fabPosition by framed(Scaffold.FabPosition.End)
}
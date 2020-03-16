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

import androidx.animation.FastOutSlowInEasing
import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.remember
import androidx.ui.animation.Transition
import androidx.ui.core.Alignment
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.common.AbsorbPointer
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.common.Scroller
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.currentTextComposableStyle
import com.ivianuu.essentials.ui.dialog.DialogRoute
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.material.AppBarStyle
import com.ivianuu.essentials.ui.material.Checkbox
import com.ivianuu.essentials.ui.material.FloatingActionButton
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.ScaffoldState
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.Surface
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.transform.TransformScale
import kotlin.time.milliseconds

@Composable
fun FabAnimation(
    visible: Boolean,
    fab: @Composable () -> Unit
) {
    Transition(
        definition = fabTransitionDefinition,
        toState = visible
    ) { state ->
        Container(
            modifier = TransformScale(
                scaleX = state[FabScale],
                scaleY = state[FabScale],
                pivotX = 0.5f,
                pivotY = 0.5f
            ),
            children = fab
        )
    }
}

private val FabScale = FloatPropKey()

private val fabTransitionDefinition = transitionDefinition {
    state(true) {
        set(FabScale, 1f)
    }
    state(false) {
        set(FabScale, 0f)
    }

    transition {
        FabScale using tween<Float> {
            duration = 120.milliseconds.toLongMilliseconds().toInt()
            easing = FastOutSlowInEasing
        }
    }
}

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
                style = AppBarStyle(
                    color = color,
                    elevation = if (alpha == 1f) 8.dp else 0.dp,
                    centerTitle = controls.centerTitle
                )
            )
        }) else null,
        fabPosition = controls.fabPosition,
        fab = {
            FabAnimation(
                visible = controls.showFab
            ) {
                FloatingActionButton(text = { Text("Click me") }, onClick = {})
            }
        },
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
                        alignment = Alignment.CenterStart,
                        padding = EdgeInsets(16.dp)
                    ) {
                        Text(
                            text = "Bottom bar",
                            style = currentTextComposableStyle()
                                .copy(textStyle = MaterialTheme.typography().h6)
                        )
                    }
                }
            }
        }) else null,
        bodyLayoutMode = controls.bodyLayoutMode,
        body = {
            Scroller {
                Subheader { Text("Top bar") }
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

                Subheader { Text("Body") }
                ListItem(
                    title = { Text("Body layout mode") },
                    onClick = {
                        navigator.push(
                            DialogRoute {
                                SingleChoiceListDialog(
                                    items = ScaffoldState.BodyLayoutMode.values().toList(),
                                    selectedItem = controls.bodyLayoutMode,
                                    onSelect = { controls.bodyLayoutMode = it },
                                    itemCallback = {
                                        Text(it.name)
                                    }
                                )
                            }
                        )
                    }
                )

                Subheader { Text("Bottom bar") }
                ListItem(
                    title = { Text("Show bottom bar") },
                    trailing = {
                        AbsorbPointer {
                            Checkbox(checked = controls.showBottomBar, onCheckedChange = {})
                        }
                    },
                    onClick = { controls.showBottomBar = !controls.showBottomBar }
                )

                Subheader { Text("Fab") }
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
                                    itemCallback = {
                                        Text(it.name)
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }
    )
}

@Model
private class ScaffoldControls(
    var showTopAppBar: Boolean = true,
    var centerTitle: Boolean = false,
    var bodyLayoutMode: ScaffoldState.BodyLayoutMode = ScaffoldState.BodyLayoutMode.Wrap,
    var showBottomBar: Boolean = false,
    var showFab: Boolean = false,
    var fabPosition: ScaffoldState.FabPosition = ScaffoldState.FabPosition.End
)
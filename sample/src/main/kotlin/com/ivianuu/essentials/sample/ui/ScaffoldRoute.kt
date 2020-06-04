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
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.ui.animation.Transition
import androidx.ui.core.Modifier
import androidx.ui.core.TransformOrigin
import androidx.ui.core.drawLayer
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.common.absorbPointer
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.DialogRoute
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.material.Checkbox
import com.ivianuu.essentials.ui.material.FloatingActionButton
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.PrimaryAppBarStyle
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.ScaffoldState
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.Surface
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route
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
        Box(
            modifier = Modifier.drawLayer(
                scaleX = state[FabScale],
                scaleY = state[FabScale],
                transformOrigin = TransformOrigin(0.5f, 0.5f)
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
            TopAppBar(
                title = { Text("Scaffold") },
                style = PrimaryAppBarStyle(
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
            Surface(color = MaterialTheme.colors.primary) {
                SafeArea(
                    top = false,
                    left = false,
                    right = false,
                    bottom = true
                ) {
                    Box(
                        modifier = Modifier.preferredHeight(56.dp)
                            .fillMaxWidth()
                            .padding(16.dp),
                        gravity = ContentGravity.CenterStart
                    ) {
                        Text(
                            text = "Bottom bar",
                            textStyle = MaterialTheme.typography.h6
                        )
                    }
                }
            }
        }) else null,
        body = {
            VerticalScroller {
                Subheader { Text("Top bar") }
                ListItem(
                    title = { Text("Show top bar") },
                    trailing = {
                        Checkbox(
                            checked = controls.showTopAppBar,
                            modifier = Modifier.absorbPointer(),
                            onCheckedChange = {}
                        )

                    },
                    onClick = { controls.showTopAppBar = !controls.showTopAppBar }
                )
                ListItem(
                    title = { Text("Center title") },
                    trailing = {
                        Checkbox(
                            checked = controls.centerTitle,
                            modifier = Modifier.absorbPointer(),
                            onCheckedChange = {}
                        )
                    },
                    onClick = { controls.centerTitle = !controls.centerTitle }
                )

                Subheader { Text("Bottom bar") }
                ListItem(
                    title = { Text("Show bottom bar") },
                    trailing = {
                        Checkbox(
                            checked = controls.showBottomBar,
                            modifier = Modifier.absorbPointer(),
                            onCheckedChange = {}
                        )
                    },
                    onClick = { controls.showBottomBar = !controls.showBottomBar }
                )

                Subheader { Text("Fab") }
                ListItem(
                    title = { Text("Show fab") },
                    trailing = {
                        Checkbox(
                            checked = controls.showFab,
                            modifier = Modifier.absorbPointer(),
                            onCheckedChange = {}
                        )
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

private class ScaffoldControls {
    var showTopAppBar by mutableStateOf(true)
    var centerTitle by mutableStateOf(false)
    var showBottomBar by mutableStateOf(false)
    var showFab by mutableStateOf(false)
    var fabPosition by mutableStateOf(ScaffoldState.FabPosition.End)
}

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
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.material.Checkbox
import androidx.ui.material.ExtendedFloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.common.RetainedScrollerPosition
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.common.absorbPointer
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.ScaffoldState
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.Surface
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.DialogRoute
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Transient
import kotlin.time.milliseconds

@Transient
class ScaffoldPage(
    private val navigator: Navigator
) {
    @Composable
    operator fun invoke() {
        val controls = remember { ScaffoldControls() }

        Scaffold(
            topAppBar = if (controls.showTopAppBar) ({
                TopAppBar(title = { Text("Scaffold") })
            }) else null,
            fabPosition = controls.fabPosition,
            fab = {
                FabAnimation(
                    visible = controls.showFab
                ) {
                    ExtendedFloatingActionButton(text = { Text("Click me") }, onClick = {})
                }
            },
            bottomBar = if (controls.showBottomBar) ({
                Surface(color = MaterialTheme.colors.primary) {
                    SafeArea(
                        top = false,
                        start = false,
                        end = false,
                        bottom = true
                    ) {
                        Box(
                            modifier = Modifier.height(56.dp)
                                .fillMaxWidth()
                                .padding(16.dp),
                            gravity = ContentGravity.CenterStart
                        ) {
                            Text(
                                text = "Bottom bar",
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }
                }
            }) else null,
            body = {
                VerticalScroller(
                    scrollerPosition = RetainedScrollerPosition()
                ) {
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
}

private class ScaffoldControls {
    var showTopAppBar by mutableStateOf(true)
    var showBottomBar by mutableStateOf(false)
    var showFab by mutableStateOf(false)
    var fabPosition by mutableStateOf(ScaffoldState.FabPosition.End)
}

@Composable
private fun FabAnimation(
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
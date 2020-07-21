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
import androidx.animation.TweenSpec
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.ui.animation.animate
import androidx.ui.core.Modifier
import androidx.ui.core.TransformOrigin
import androidx.ui.core.composed
import androidx.ui.core.drawLayer
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.material.Checkbox
import androidx.ui.material.ExtendedFloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.common.absorbPointer
import com.ivianuu.essentials.ui.core.SystemBarsPadding
import com.ivianuu.essentials.ui.core.overlaySystemBarBgColor
import com.ivianuu.essentials.ui.core.systemBarStyle
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.ScaffoldState
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.DialogRoute
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.util.isLight
import com.ivianuu.injekt.Reader
import kotlin.time.milliseconds

@Reader
@Composable
fun ScaffoldPage() {
    val controls = remember { ScaffoldControls() }

    Scaffold(
        topBar = if (controls.showTopBar) ({
            TopAppBar(title = { Text("Scaffold") })
        }) else null,
        fabPosition = controls.fabPosition,
        fab = {
            ExtendedFloatingActionButton(
                text = { Text("Click me") },
                modifier = Modifier.fabAnimation(controls.showFab),
                onClick = {}
            )
        },
        bottomBar = if (controls.showBottomBar) ({
            Surface(
                modifier = Modifier.systemBarStyle(
                    bgColor = overlaySystemBarBgColor(MaterialTheme.colors.primary),
                    lightIcons = MaterialTheme.colors.primary.isLight
                ),
                elevation = 8.dp,
                color = MaterialTheme.colors.primary
            ) {
                SystemBarsPadding(left = false, top = false, right = false) {
                    Box(
                        modifier = Modifier.height(56.dp)
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        gravity = ContentGravity.CenterStart
                    ) {
                        Text(
                            text = "Bottom bar",
                            style = MaterialTheme.typography.h6
                        )
                    }
                }
            }
        }) else null
    ) {
        InsettingScrollableColumn {
            Subheader { Text("Top bar") }
            ListItem(
                title = { Text("Show top bar") },
                trailing = {
                    Checkbox(
                        checked = controls.showTopBar,
                        modifier = Modifier.absorbPointer(),
                        onCheckedChange = {}
                    )

                },
                onClick = { controls.showTopBar = !controls.showTopBar }
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
}

private class ScaffoldControls {
    var showTopBar by mutableStateOf(true)
    var showBottomBar by mutableStateOf(false)
    var showFab by mutableStateOf(false)
    var fabPosition by mutableStateOf(ScaffoldState.FabPosition.End)
}

fun Modifier.fabAnimation(visible: Boolean): Modifier = composed {
    val fraction = animate(
        if (visible) 1f else 0f, TweenSpec(
            durationMillis = 120.milliseconds.toLongMilliseconds().toInt(),
            easing = FastOutSlowInEasing
        )
    )
    drawLayer(
        scaleX = fraction,
        scaleY = fraction,
        transformOrigin = TransformOrigin(0.5f, 0.5f)
    )
}

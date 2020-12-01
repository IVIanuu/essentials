/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.animation.animate
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.common.absorbPointer
import com.ivianuu.essentials.ui.core.InsetsPadding
import com.ivianuu.essentials.ui.core.isLight
import com.ivianuu.essentials.ui.core.overlaySystemBarBgColor
import com.ivianuu.essentials.ui.core.systemBarStyle
import com.ivianuu.essentials.ui.coroutines.UiScope
import com.ivianuu.essentials.ui.dialog.DialogWrapper
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.navigation.popTopKeyWithResult
import com.ivianuu.essentials.ui.navigation.pushKeyForResult
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.launch
import kotlin.time.milliseconds

class ScaffoldKey

@KeyUiBinding<ScaffoldKey>
@FunBinding
@Composable
fun ScaffoldPage(
    pickFabPosition: pushKeyForResult<FabPositionKey, FabPosition>,
    uiScope: UiScope,
) {
    val controls = remember { ScaffoldControls() }

    Scaffold(
        topBar = if (controls.showTopBar) ({
            TopAppBar(title = { Text("Scaffold") })
        }) else null,
        floatingActionButtonPosition = controls.fabPosition,
        floatingActionButton = {
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
                InsetsPadding(start = false, top = false, end = false) {
                    Box(
                        modifier = Modifier.height(56.dp)
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        contentAlignment = Alignment.CenterStart
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
                    uiScope.launch {
                        pickFabPosition(FabPositionKey(controls.fabPosition))
                            ?.let { controls.fabPosition = it }
                    }
                }
            )
        }
    }
}

private class ScaffoldControls {
    var showTopBar by mutableStateOf(true)
    var showBottomBar by mutableStateOf(false)
    var showFab by mutableStateOf(false)
    var fabPosition by mutableStateOf(FabPosition.End)
}

fun Modifier.fabAnimation(visible: Boolean): Modifier = composed {
    val fraction = animate(
        if (visible) 1f else 0f, TweenSpec(
            durationMillis = 120.milliseconds.toLongMilliseconds().toInt(),
            easing = FastOutSlowInEasing
        )
    )
    graphicsLayer(
        scaleX = fraction,
        scaleY = fraction,
        transformOrigin = TransformOrigin(0.5f, 0.5f)
    )
}

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

package com.ivianuu.essentials.ui.material

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import androidx.ui.core.Constraints
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.id
import androidx.ui.core.layoutId
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxSize
import androidx.ui.material.DrawerState
import androidx.ui.material.ModalDrawerLayout
import androidx.ui.material.Surface
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.common.onBackPressed
import com.ivianuu.essentials.ui.core.ConsumeInsets
import com.ivianuu.essentials.ui.core.InsetsPadding
import com.ivianuu.essentials.ui.core.SystemBarsPadding

@Composable
fun Scaffold(
    fabPosition: ScaffoldState.FabPosition = ScaffoldState.FabPosition.End,
    applyInsets: Boolean = true,
    drawerContent: @Composable (() -> Unit)? = null,
    topBar: @Composable (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    fab: @Composable (() -> Unit)? = null,
    body: @Composable (() -> Unit)? = null
) {
    val scaffoldState = remember { ScaffoldState() }
    scaffoldState.fabPosition = fabPosition
    scaffoldState.applyInsets = applyInsets
    Scaffold(
        state = scaffoldState,
        drawerContent = drawerContent,
        topBar = topBar,
        body = body,
        bottomBar = bottomBar,
        fab = fab
    )
}

@Composable
fun Scaffold(
    state: ScaffoldState,
    drawerContent: @Composable (() -> Unit)? = null,
    topBar: @Composable (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    fab: @Composable (() -> Unit)? = null,
    body: @Composable (() -> Unit)? = null
) {
    // update state
    state.hasTopBar = topBar != null
    state.hasDrawer = drawerContent != null
    state.hasBody = body != null
    state.hasBottomBar = bottomBar != null
    state.hasFab = fab != null

    if (state.isDrawerOpen) {
        onBackPressed { state.isDrawerOpen = false }
    }

    Providers(ScaffoldAmbient provides state) {
        var layout: @Composable () -> Unit = {
            Surface {
                ScaffoldLayout(state = state) {
                    if (topBar != null) {
                        Stack(modifier = Modifier.layoutId(ScaffoldSlot.TopAppBar)) {
                            topBar()
                        }
                    }

                    if (body != null) {
                        ConsumeInsets(
                            top = state.applyInsets && state.hasTopBar,
                            bottom = state.applyInsets && state.hasBottomBar
                        ) {
                            Stack(modifier = Modifier.layoutId(ScaffoldSlot.Body)) {
                                body()
                            }
                        }
                    }

                    if (bottomBar != null) {
                        Stack(modifier = Modifier.layoutId(ScaffoldSlot.BottomBar)) {
                            bottomBar()
                        }
                    }

                    if (fab != null) {
                        SystemBarsPadding(
                            modifier = Modifier.layoutId(ScaffoldSlot.Fab),
                            top = state.applyInsets && !state.hasTopBar,
                            bottom = state.applyInsets && !state.hasBottomBar
                        ) {
                            Stack {
                                fab()
                            }
                        }
                    }
                }
            }
        }

        if (drawerContent != null) {
            val tmp = layout
            layout = {
                ModalDrawerLayout(
                    drawerState = if (state.isDrawerOpen) DrawerState.Opened else DrawerState.Closed,
                    onStateChange = { state.isDrawerOpen = it == DrawerState.Opened },
                    gesturesEnabled = state.isDrawerGesturesEnabled,
                    drawerContent = {
                        Surface {
                            drawerContent()
                        }
                    },
                    bodyContent = tmp
                )
            }
        }

        InsetsPadding(
            modifier = Modifier.fillMaxSize(),
            left = state.applyInsets,
            right = state.applyInsets,
            top = false,
            bottom = false,
            children = layout
        )
    }
}

class ScaffoldState {

    var hasTopBar by mutableStateOf(false)
        internal set
    var hasDrawer by mutableStateOf(false)
        internal set
    var hasBody by mutableStateOf(false)
        internal set
    var hasBottomBar by mutableStateOf(false)
        internal set
    var hasFab by mutableStateOf(false)
        internal set

    var isDrawerOpen by mutableStateOf(false)
    var isDrawerGesturesEnabled by mutableStateOf(false)

    var fabPosition by mutableStateOf(FabPosition.End)

    var applyInsets by mutableStateOf(true)

    enum class FabPosition { Center, End }

}

val ScaffoldAmbient =
    staticAmbientOf<ScaffoldState>()

@Composable
private fun ScaffoldLayout(
    state: ScaffoldState,
    children: @Composable () -> Unit
) {
    Layout(children = children) { measurables, incomingConstraints, _ ->
        val width = incomingConstraints.maxWidth
        val height = incomingConstraints.maxHeight

        val topAppBarMeasureable = measurables.firstOrNull {
            it.id == ScaffoldSlot.TopAppBar
        }
        val bodyMeasureable = measurables.firstOrNull {
            it.id == ScaffoldSlot.Body
        }
        val bottomBarMeasureable = measurables.firstOrNull {
            it.id == ScaffoldSlot.BottomBar
        }
        val fabMeasureable = measurables.firstOrNull {
            it.id == ScaffoldSlot.Fab
        }

        var barConstraints = incomingConstraints.copy(
            minWidth = width,
            maxWidth = width,
            minHeight = 0
        )

        val topAppBarPlaceable = topAppBarMeasureable
            ?.measure(barConstraints)
            ?.also { placeable ->
                barConstraints =
                    barConstraints.copy(maxHeight = barConstraints.maxHeight - placeable.height)
            }
        val topAppBarTop = if (topAppBarPlaceable != null) 0 else null
        val topAppBarBottom =
            if (topAppBarPlaceable != null) topAppBarTop!! + topAppBarPlaceable.height else null

        val bottomBarPlaceable = bottomBarMeasureable?.measure(barConstraints)
        val bottomBarBottom = if (bottomBarPlaceable != null) height else null
        val bottomBarTop =
            if (bottomBarPlaceable != null) bottomBarBottom!! - bottomBarPlaceable.height else null

        val bodyTop = if (topAppBarMeasureable != null) topAppBarBottom!! else 0
        val bodyBottom = if (bottomBarMeasureable != null) bottomBarTop!! else height

        val bodyHeight = if (bodyMeasureable != null) bodyBottom - bodyTop else null

        val bodyPlaceable = if (bodyMeasureable == null) {
            null
        } else {
            val bodyConstraints = Constraints(
                minWidth = width,
                maxWidth = width,
                minHeight = bodyHeight!!,
                maxHeight = bodyHeight
            )

            bodyMeasureable.measure(bodyConstraints)
        }

        val fabPlaceable =
            fabMeasureable?.measure(incomingConstraints.copy(minWidth = 0, minHeight = 0))

        val fabPadding = 16.dp.toIntPx()

        val fabTop = if (fabPlaceable != null) {
            if (bottomBarMeasureable != null) bottomBarTop!! - fabPlaceable.height - fabPadding
            else height - fabPlaceable.height - fabPadding
        } else null
        val fabLeft = if (fabPlaceable != null) {
            when (state.fabPosition) {
                ScaffoldState.FabPosition.Center -> width / 2 - fabPlaceable.width / 2
                ScaffoldState.FabPosition.End -> width - fabPlaceable.width - fabPadding
            }
        } else null

        layout(width, height) {
            bodyPlaceable?.place(0, bodyTop)
            fabPlaceable?.place(fabLeft!!, fabTop!!)
            bottomBarPlaceable?.place(0, bottomBarTop!!)
            topAppBarPlaceable?.place(0, topAppBarTop!!)
        }
    }
}

private enum class ScaffoldSlot { TopAppBar, Body, BottomBar, Fab }

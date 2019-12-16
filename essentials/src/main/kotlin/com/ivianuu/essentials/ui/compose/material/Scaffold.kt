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

package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.remember
import androidx.ui.core.Constraints
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import androidx.ui.core.dp
import androidx.ui.core.looseMin
import androidx.ui.material.DrawerState
import androidx.ui.material.ModalDrawerLayout
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.common.withDensity
import com.ivianuu.essentials.ui.compose.layout.Expand

@Composable
fun Scaffold(
    drawerContent: (@Composable() () -> Unit)? = null,
    topAppBar: (@Composable() () -> Unit)? = null,
    body: (@Composable() () -> Unit)? = null,
    bottomBar: (@Composable() () -> Unit)? = null,
    fab: (@Composable() () -> Unit)? = null,
    fabPosition: ScaffoldState.FabPosition = ScaffoldState.FabPosition.End,
    bodyLayoutMode: ScaffoldState.BodyLayoutMode = ScaffoldState.BodyLayoutMode.Wrap
) {
    val scaffoldState = remember { ScaffoldState() }
    remember(fabPosition) { scaffoldState.fabPosition = fabPosition }
    remember(bodyLayoutMode) { scaffoldState.bodyLayoutMode = bodyLayoutMode }

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = drawerContent,
        topAppBar = topAppBar,
        body = body,
        bottomBar = bottomBar,
        fab = fab
    )
}

@Composable
fun Scaffold(
    scaffoldState: ScaffoldState,
    drawerContent: (@Composable() () -> Unit)? = null,
    topAppBar: (@Composable() () -> Unit)? = null,
    body: (@Composable() () -> Unit)? = null,
    bottomBar: (@Composable() () -> Unit)? = null,
    fab: (@Composable() () -> Unit)? = null
) {
    // update state
    scaffoldState.hasTopAppBar = topAppBar != null
    scaffoldState.hasDrawer = drawerContent != null
    scaffoldState.hasBody = body != null
    scaffoldState.hasBottomBar = bottomBar != null
    scaffoldState.hasFab = fab != null

    ScaffoldAmbient.Provider(value = scaffoldState) {
        Expand {
            ModalDrawerLayout(
                drawerState = scaffoldState.drawerState,
                onStateChange = { scaffoldState.drawerState = it },
                gesturesEnabled = drawerContent != null,
                drawerContent = {
                    if (drawerContent != null) {
                        EsSurface {
                            drawerContent()
                        }
                    }
                },
                bodyContent = {
                    EsSurface {
                        ScaffoldLayout(
                            state = scaffoldState,
                            topAppBar = topAppBar,
                            body = body,
                            bottomBar = bottomBar,
                            fab = fab
                        )
                    }
                }
            )
        }
    }
}

class ScaffoldState {

    var hasTopAppBar = false
        internal set
    var hasDrawer = false
        internal set
    var hasBody = false
        internal set
    var hasBottomBar = false
        internal set
    var hasFab = false
        internal set

    var drawerState by framed(DrawerState.Closed)

    var fabPosition by framed(FabPosition.End)
    var bodyLayoutMode by framed(BodyLayoutMode.Wrap)

    fun toggleDrawer() {
        if (drawerState != DrawerState.Opened) {
            openDrawer()
        } else {
            closeDrawer()
        }
    }

    fun openDrawer() {
        check(hasDrawer)
        drawerState = DrawerState.Opened
    }

    fun closeDrawer() {
        check(hasDrawer)
        drawerState = DrawerState.Closed
    }

    enum class BodyLayoutMode { ExtendTop, ExtendBottom, ExtendBoth, Wrap }

    enum class FabPosition { Center, End }
}

private val ScaffoldAmbient = Ambient.of<ScaffoldState>()
@Composable
val scaffold: ScaffoldState
    get() = ambient(ScaffoldAmbient)

@Composable
private fun ScaffoldLayout(
    state: ScaffoldState,
    topAppBar: (@Composable() () -> Unit)?,
    body: (@Composable() () -> Unit)?,
    bottomBar: (@Composable() () -> Unit)?,
    fab: (@Composable() () -> Unit)?
) {
    val children: @Composable() () -> Unit = {
        if (topAppBar != null) {
            ParentData(ScaffoldLayoutSlot.TopAppBar) {
                topAppBar()
            }
        }

        if (body != null) {
            ParentData(ScaffoldLayoutSlot.Body) {
                body()
            }
        }

        if (bottomBar != null) {
            ParentData(ScaffoldLayoutSlot.BottomBar) {
                bottomBar()
            }
        }

        if (fab != null) {
            ParentData(ScaffoldLayoutSlot.Fab) {
                fab()
            }
        }
    }

    val fabPadding = withDensity { 16.dp.toIntPx() }

    Layout(children = children) { measureables, incomingConstraints ->
        val width = incomingConstraints.maxWidth
        val height = incomingConstraints.maxHeight

        val topAppBarMeasureable = measureables.firstOrNull {
            it.parentData == ScaffoldLayoutSlot.TopAppBar
        }
        val bodyMeasureable = measureables.firstOrNull {
            it.parentData == ScaffoldLayoutSlot.Body
        }
        val bottomBarMeasureable = measureables.firstOrNull {
            it.parentData == ScaffoldLayoutSlot.BottomBar
        }
        val fabMeasureable = measureables.firstOrNull {
            it.parentData == ScaffoldLayoutSlot.Fab
        }

        var barConstraints = incomingConstraints.copy(
            minWidth = width,
            maxWidth = width,
            minHeight = IntPx.Zero
        )

        val topAppBarPlaceable = topAppBarMeasureable
            ?.measure(barConstraints)
            ?.also { placeable ->
                barConstraints =
                    barConstraints.copy(maxHeight = barConstraints.maxHeight - placeable.height)
            }
        val topAppBarTop = if (topAppBarPlaceable != null) IntPx.Zero else null
        val topAppBarBottom =
            if (topAppBarPlaceable != null) topAppBarTop!! + topAppBarPlaceable.height else null

        val bottomBarPlaceable = bottomBarMeasureable?.measure(barConstraints)
        val bottomBarBottom = if (bottomBarPlaceable != null) height else null
        val bottomBarTop =
            if (bottomBarPlaceable != null) bottomBarBottom!! - bottomBarPlaceable.height else null

        val bodyTop: IntPx?
        val bodyBottom: IntPx?

        if (bodyMeasureable == null) {
            bodyTop = null
            bodyBottom = null
        } else {
            when (state.bodyLayoutMode) {
                ScaffoldState.BodyLayoutMode.ExtendBoth -> {
                    bodyTop = topAppBarTop!!
                    bodyBottom = bottomBarBottom!!
                }
                ScaffoldState.BodyLayoutMode.ExtendTop -> {
                    bodyTop = topAppBarTop!!
                    bodyBottom = if (bottomBarMeasureable != null) bottomBarTop!! else height
                }
                ScaffoldState.BodyLayoutMode.ExtendBottom -> {
                    bodyTop = if (topAppBarMeasureable != null) topAppBarBottom!! else IntPx.Zero
                    bodyBottom = bottomBarBottom!!
                }
                ScaffoldState.BodyLayoutMode.Wrap -> {
                    bodyTop = if (topAppBarMeasureable != null) topAppBarBottom!! else IntPx.Zero
                    bodyBottom = if (bottomBarMeasureable != null) bottomBarTop!! else height
                }
            }
        }

        val bodyHeight = if (bodyMeasureable != null) bodyBottom!! - bodyTop!! else null

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

        val fabPlaceable = fabMeasureable?.measure(incomingConstraints.looseMin())

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
            bodyPlaceable?.place(IntPx.Zero, bodyTop!!)
            fabPlaceable?.place(fabLeft!!, fabTop!!)
            bottomBarPlaceable?.place(IntPx.Zero, bottomBarTop!!)
            topAppBarPlaceable?.place(IntPx.Zero, topAppBarTop!!)
        }
    }
}

private enum class ScaffoldLayoutSlot { TopAppBar, Body, BottomBar, Fab }

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
import androidx.compose.State
import androidx.ui.core.Constraints
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import androidx.ui.core.dp
import androidx.ui.core.looseMin
import androidx.ui.material.DrawerState
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.invokeAsComposable
import com.ivianuu.essentials.ui.compose.core.memo
import com.ivianuu.essentials.ui.compose.core.state
import com.ivianuu.essentials.ui.compose.core.withDensity
import com.ivianuu.essentials.ui.compose.layout.Expand

@Composable
fun Scaffold(
    drawer: (@Composable() (
        drawerState: DrawerState,
        onDrawerStateChanged: (DrawerState) -> Unit,
        body: @Composable() () -> Unit
    ) -> Unit
    )? = null,
    drawerState: State<DrawerState> = state { DrawerState.Closed },
    topAppBar: (@Composable() () -> Unit)? = null,
    body: (@Composable() () -> Unit)? = null,
    bottomBar: (@Composable() () -> Unit)? = null,
    fab: (@Composable() () -> Unit)? = null,
    fabPosition: Scaffold.FabPosition = Scaffold.FabPosition.End,
    bodyLayoutMode: Scaffold.BodyLayoutMode = Scaffold.BodyLayoutMode.Wrap
) = composable {
    val scaffold = memo { Scaffold(drawerState) }

    // update state
    scaffold.hasTopAppBar = topAppBar != null
    scaffold.hasDrawer = drawer != null
    scaffold.hasBody = body != null
    scaffold.hasBottomBar = bottomBar != null
    scaffold.hasFab = fab != null

    val scaffoldLayout: @Composable() () -> Unit = {
        ScaffoldLayout(
            topAppBar = topAppBar,
            body = body,
            bottomBar = bottomBar,
            fab = fab,
            bodyLayoutMode = bodyLayoutMode,
            fabPosition = fabPosition
        )
    }


    EsSurface {
        ScaffoldAmbient.Provider(value = scaffold) {
            Expand {
                if (drawer != null) {
                    drawer(
                        drawerState.value,
                        { drawerState.value = it },
                        scaffoldLayout
                    )
                } else {
                    scaffoldLayout()
                }
            }
        }
    }
}

val ScaffoldAmbient = Ambient.of<Scaffold>()

class Scaffold internal constructor(_drawerState: State<DrawerState>) {

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

    // todo use by framed()
    var drawerState by _drawerState

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

    enum class FabPosition {
        Center, End
    }

}

@Composable
private fun ScaffoldLayout(
    topAppBar: @Composable() (() -> Unit)?,
    body: @Composable() (() -> Unit)?,
    bodyLayoutMode: Scaffold.BodyLayoutMode,
    bottomBar: @Composable() (() -> Unit)?,
    fab: @Composable() (() -> Unit)?,
    fabPosition: Scaffold.FabPosition
) = composable {
    val children: @Composable() () -> Unit = {
        if (topAppBar != null) {
            ParentData(ScaffoldLayoutSlot.TopAppBar) {
                topAppBar.invokeAsComposable()
            }
        }

        if (body != null) {
            ParentData(ScaffoldLayoutSlot.Body) {
                body.invokeAsComposable()
            }
        }

        if (bottomBar != null) {
            ParentData(ScaffoldLayoutSlot.BottomBar) {
                bottomBar.invokeAsComposable()
            }
        }

        if (fab != null) {
            ParentData(ScaffoldLayoutSlot.Fab) {
                fab.invokeAsComposable()
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
            when (bodyLayoutMode) {
                Scaffold.BodyLayoutMode.ExtendBoth -> {
                    bodyTop = topAppBarTop!!
                    bodyBottom = bottomBarBottom!!
                }
                Scaffold.BodyLayoutMode.ExtendTop -> {
                    bodyTop = topAppBarTop!!
                    bodyBottom = if (bottomBarMeasureable != null) bottomBarTop!! else height
                }
                Scaffold.BodyLayoutMode.ExtendBottom -> {
                    bodyTop = if (topAppBarMeasureable != null) topAppBarBottom!! else IntPx.Zero
                    bodyBottom = bottomBarBottom!!
                }
                Scaffold.BodyLayoutMode.Wrap -> {
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
            when (fabPosition) {
                Scaffold.FabPosition.Center -> width / 2 - fabPlaceable.width / 2
                Scaffold.FabPosition.End -> width - fabPlaceable.width - fabPadding
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
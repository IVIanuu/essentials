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
import androidx.compose.memo
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Constraints
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import androidx.ui.core.dp
import androidx.ui.layout.Padding
import androidx.ui.layout.Stack
import androidx.ui.material.DrawerState
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.layout.Expand

@Composable
fun Scaffold(
    drawer: (@Composable() (
        drawerState: DrawerState,
        onDrawerStateChanged: (DrawerState) -> Unit,
        body: @Composable() () -> Unit
    ) -> Unit
    )? = null,
    drawerState: State<DrawerState> = +state { DrawerState.Closed },
    topAppBar: (@Composable() () -> Unit)? = null,
    body: (@Composable() () -> Unit)? = null,
    bottomBar: (@Composable() () -> Unit)? = null,
    bodyLayoutMode: Scaffold.BodyLayoutMode = Scaffold.BodyLayoutMode.Wrap,
    fabConfiguration: Scaffold.FabConfiguration? = null
) = composable("Scaffold") {
    val scaffold = +memo { Scaffold(drawerState) }

    // update state
    scaffold.hasTopAppBar = topAppBar != null
    scaffold.hasDrawer = drawer != null
    scaffold.hasBody = body != null
    scaffold.hasBottomBar = bottomBar != null
    scaffold.hasFab = fabConfiguration != null

    val finalLayout: @Composable() () -> Unit = {
        Stack {
            expanded {
                ScaffoldBodyAndBarsLayout(
                    topAppBar = topAppBar,
                    body = body,
                    bottomBar = bottomBar,
                    bodyLayoutMode = bodyLayoutMode
                )
            }

            if (fabConfiguration != null) {
                aligned(
                    alignment = when (fabConfiguration.position) {
                        Scaffold.FabPosition.Center -> Alignment.BottomCenter
                        Scaffold.FabPosition.End -> Alignment.BottomRight
                    }
                ) {
                    Padding(padding = 16.dp) {
                        fabConfiguration.fab()
                    }
                }
            }
        }
    }


    EsSurface {
        ScaffoldAmbient.Provider(value = scaffold) {
            Expand {
                if (drawer != null) {
                    drawer(
                        drawerState.value,
                        { drawerState.value = it },
                        finalLayout
                    )
                } else {
                    finalLayout()
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

    data class FabConfiguration(
        val position: FabPosition,
        val fab: @Composable() () -> Unit
    )

    enum class FabPosition {
        Center, End
    }

    enum class BodyLayoutMode { ExtendTop, ExtendBottom, ExtendBoth, Wrap }

}

@Composable
private fun ScaffoldBodyAndBarsLayout(
    topAppBar: @Composable() (() -> Unit)?,
    body: @Composable() (() -> Unit)?,
    bottomBar: @Composable() (() -> Unit)?,
    bodyLayoutMode: Scaffold.BodyLayoutMode
) = composable("ScaffoldBodyAndBarsLayout") {
    val children: @Composable() () -> Unit = {
        if (topAppBar != null) {
            ParentData(ScaffoldBodySlot.TopAppBar) {
                topAppBar()
            }
        }

        if (body != null) {
            ParentData(ScaffoldBodySlot.Body) {
                body()
            }
        }

        if (bottomBar != null) {
            ParentData(ScaffoldBodySlot.BottomBar) {
                bottomBar()
            }
        }
    }

    Layout(children = children) { measureables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight

        val topAppBarMeasureable = measureables.firstOrNull {
            it.parentData == ScaffoldBodySlot.TopAppBar
        }
        val bodyMeasureable = measureables.firstOrNull {
            it.parentData == ScaffoldBodySlot.Body
        }
        val bottomBarMeasureable = measureables.firstOrNull {
            it.parentData == ScaffoldBodySlot.BottomBar
        }

        var childConstraints = constraints.copy(
            minWidth = width,
            maxWidth = width,
            minHeight = IntPx.Zero
        )

        val topAppBarPlaceable = topAppBarMeasureable
            ?.measure(childConstraints)
            ?.also { placeable ->
                childConstraints =
                    childConstraints.copy(maxHeight = childConstraints.maxHeight - placeable.height)
            }
        val topAppBarTop = if (topAppBarPlaceable != null) IntPx.Zero else null
        val topAppBarBottom =
            if (topAppBarPlaceable != null) topAppBarTop!! + topAppBarPlaceable.height else null

        val bottomBarPlaceable = bottomBarMeasureable?.measure(childConstraints)
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

        layout(constraints.maxWidth, constraints.maxHeight) {
            bodyPlaceable?.place(IntPx.Zero, bodyTop!!)
            bottomBarPlaceable?.place(IntPx.Zero, bottomBarTop!!)
            topAppBarPlaceable?.place(IntPx.Zero, topAppBarTop!!)
        }
    }
}

private enum class ScaffoldBodySlot { TopAppBar, Body, BottomBar }
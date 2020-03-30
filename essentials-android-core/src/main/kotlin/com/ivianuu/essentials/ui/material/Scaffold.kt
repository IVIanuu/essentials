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
import androidx.compose.Model
import androidx.compose.Providers
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import androidx.ui.core.Constraints
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import androidx.ui.layout.Stack
import androidx.ui.material.DrawerState
import androidx.ui.material.ModalDrawerLayout
import androidx.ui.unit.IntPx
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.common.onBackPressed

@Composable
fun Scaffold(
    drawerContent: @Composable (() -> Unit)? = null,
    topAppBar: @Composable (() -> Unit)? = null,
    body: @Composable (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    fab: @Composable (() -> Unit)? = null,
    fabPosition: ScaffoldState.FabPosition = ScaffoldState.FabPosition.End,
    bodyLayoutMode: ScaffoldState.BodyLayoutMode = ScaffoldState.BodyLayoutMode.Wrap,
    applySideSafeArea: Boolean = true
) {
    val scaffoldState = remember { ScaffoldState() }
    remember(fabPosition) { scaffoldState.fabPosition = fabPosition }
    remember(bodyLayoutMode) { scaffoldState.bodyLayoutMode = bodyLayoutMode }
    remember(applySideSafeArea) { scaffoldState.applySideSafeArea = applySideSafeArea }

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
    drawerContent: @Composable (() -> Unit)? = null,
    topAppBar: @Composable (() -> Unit)? = null,
    body: @Composable (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    fab: @Composable (() -> Unit)? = null
) {
    // update state
    remember(topAppBar) { scaffoldState.hasTopAppBar = topAppBar != null }
    remember(drawerContent) { scaffoldState.hasDrawer = drawerContent != null }
    remember(body) { scaffoldState.hasBody = body != null }
    remember(bottomBar) { scaffoldState.hasBottomBar = bottomBar != null }
    remember(fab) { scaffoldState.hasFab = fab != null }

    if (scaffoldState.isDrawerOpen) {
        onBackPressed { scaffoldState.isDrawerOpen = false }
    }

    Providers(ScaffoldAmbient provides scaffoldState) {
        var layout: @Composable () -> Unit = {
            Surface {
                ScaffoldLayout(
                    state = scaffoldState,
                    topAppBar = topAppBar?.let {
                        {
                            Stack {
                                topAppBar()
                            }
                        }
                    },
                    body = body?.let {
                        {
                            Stack {
                                body()
                            }
                        }
                    },
                    bottomBar = bottomBar?.let {
                        {
                            Stack {
                                bottomBar()
                            }
                        }
                    },
                    fab = fab?.let {
                        {
                            Stack {
                                if (bottomBar != null) {
                                    fab()
                                } else {
                                    SafeArea(children = fab)
                                }
                            }
                        }
                    }
                )
            }
        }

        if (drawerContent != null) {
            val tmp = layout
            layout = {
                ModalDrawerLayout(
                    drawerState = if (scaffoldState.isDrawerOpen) DrawerState.Opened else DrawerState.Closed,
                    onStateChange = { scaffoldState.isDrawerOpen = it == DrawerState.Opened },
                    gesturesEnabled = scaffoldState.isDrawerGesturesEnabled,
                    drawerContent = {
                        Surface {
                            drawerContent()
                        }
                    },
                    bodyContent = tmp
                )
            }
        }

        SafeArea(
            modifier = LayoutSize.Fill,
            left = scaffoldState.applySideSafeArea,
            top = false,
            right = scaffoldState.applySideSafeArea,
            bottom = false,
            children = layout
        )
    }
}

@Model
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

    var isDrawerOpen = false
    var isDrawerGesturesEnabled = false

    var fabPosition = FabPosition.End
    var bodyLayoutMode = BodyLayoutMode.Wrap
    var applySideSafeArea = true

    enum class BodyLayoutMode { ExtendTop, ExtendBottom, ExtendBoth, Wrap }

    enum class FabPosition { Center, End }
}

val ScaffoldAmbient =
    staticAmbientOf<ScaffoldState>()

@Composable
private fun ScaffoldLayout(
    state: ScaffoldState,
    topAppBar: @Composable (() -> Unit)?,
    body: @Composable (() -> Unit)?,
    bottomBar: @Composable (() -> Unit)?,
    fab: @Composable (() -> Unit)?
) {
    val children: @Composable () -> Unit = {
        if (topAppBar != null) {
            ParentData(ScaffoldSlot.TopAppBar) {
                topAppBar()
            }
        }

        if (body != null) {
            ParentData(ScaffoldSlot.Body) {
                body()
            }
        }

        if (bottomBar != null) {
            ParentData(ScaffoldSlot.BottomBar) {
                bottomBar()
            }
        }

        if (fab != null) {
            ParentData(ScaffoldSlot.Fab) {
                fab()
            }
        }
    }

    val fabPadding = with(DensityAmbient.current) { 16.dp.toIntPx() }

    Layout(children = children) { measurables, incomingConstraints, _ ->
        val width = incomingConstraints.maxWidth
        val height = incomingConstraints.maxHeight

        val topAppBarMeasureable = measurables.firstOrNull {
            it.parentData == ScaffoldSlot.TopAppBar
        }
        val bodyMeasureable = measurables.firstOrNull {
            it.parentData == ScaffoldSlot.Body
        }
        val bottomBarMeasureable = measurables.firstOrNull {
            it.parentData == ScaffoldSlot.BottomBar
        }
        val fabMeasureable = measurables.firstOrNull {
            it.parentData == ScaffoldSlot.Fab
        }

        var barConstraints = incomingConstraints.copy(
            minWidth = width,
            maxWidth = width,
            minHeight = 0.ipx
        )

        val topAppBarPlaceable = topAppBarMeasureable
            ?.measure(barConstraints)
            ?.also { placeable ->
                barConstraints =
                    barConstraints.copy(maxHeight = barConstraints.maxHeight - placeable.height)
            }
        val topAppBarTop = if (topAppBarPlaceable != null) 0.ipx else null
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
                    bodyTop = if (topAppBarMeasureable != null) topAppBarBottom!! else 0.ipx
                    bodyBottom = bottomBarBottom!!
                }
                ScaffoldState.BodyLayoutMode.Wrap -> {
                    bodyTop = if (topAppBarMeasureable != null) topAppBarBottom!! else 0.ipx
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

        val fabPlaceable = fabMeasureable?.measure(incomingConstraints.copy(minWidth = 0.ipx, minHeight = 0.ipx))

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
            bodyPlaceable?.place(0.ipx, bodyTop!!)
            fabPlaceable?.place(fabLeft!!, fabTop!!)
            bottomBarPlaceable?.place(0.ipx, bottomBarTop!!)
            topAppBarPlaceable?.place(0.ipx, topAppBarTop!!)
        }
    }
}

private enum class ScaffoldSlot { TopAppBar, Body, BottomBar, Fab }

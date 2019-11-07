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
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.OnPositioned
import androidx.ui.core.dp
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.layout.Stack
import androidx.ui.material.DrawerState
import androidx.ui.material.surface.Surface
import com.ivianuu.essentials.ui.compose.core.composable

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
    fabConfiguration: Scaffold.FabConfiguration? = null
) = composable("Scaffold") {
    val overlays = +state { emptyList<Overlay>() }
    val scaffold = +memo { Scaffold(overlays, drawerState) }

    // update state
    scaffold.hasTopAppBar = topAppBar != null
    scaffold.hasDrawer = drawer != null
    scaffold.hasBody = body != null
    scaffold.hasBottomBar = bottomBar != null
    scaffold.hasFab = fabConfiguration != null

    ScaffoldAmbient.Provider(value = scaffold) {
        Stack {
            OnPositioned { scaffold.coordinates = it }

            val finalBody: @Composable() () -> Unit = {
                Column {
                    if (topAppBar != null) {
                        Container(modifier = Inflexible) {
                            topAppBar()
                        }
                    }

                    if (body != null) {
                        Container(
                            alignment = Alignment.TopLeft,
                            modifier = Flexible(1f)
                        ) {
                            Surface {
                                body()
                            }
                        }
                    }

                    if (bottomBar != null) {
                        Container(modifier = Inflexible) {
                            bottomBar()
                        }
                    }
                }

                if (fabConfiguration != null) {
                    aligned(
                        when (fabConfiguration.position) {
                            Scaffold.FabPosition.Center -> Alignment.BottomCenter
                            Scaffold.FabPosition.End -> Alignment.BottomRight
                        }
                    ) {
                        Padding(padding = 16.dp) {
                            fabConfiguration.fab()
                        }
                    }
                }

                // show overlays
                overlays.value
                    .filter { it.inBody }
                    .forEach { overlay ->
                        overlay.composable {
                            scaffold.removeOverlay(overlay)
                        }
                    }
            }

            if (drawer != null) {
                drawer(
                    drawerState.value,
                    { drawerState.value = it },
                    finalBody
                )
            } else {
                finalBody()
            }

            // show overlays
            overlays.value
                .filterNot { it.inBody }
                .forEach { overlay ->
                    overlay.composable {
                        scaffold.removeOverlay(overlay)
                    }
            }
        }
    }
}

val ScaffoldAmbient = Ambient.of<Scaffold>()

internal data class Overlay(
    val composable: @Composable() (() -> Unit) -> Unit,
    val inBody: Boolean
)

class Scaffold internal constructor(
    private val overlays: State<List<Overlay>>,
    _drawerState: State<DrawerState>
) {

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

    var coordinates: LayoutCoordinates? = null
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

    fun addOverlay(inBody: Boolean = true, block: (() -> Unit) -> Unit) {
        val newOverlays = overlays.value.toMutableList()
        newOverlays += Overlay(block, inBody)
        overlays.value = newOverlays
    }

    internal fun removeOverlay(overlay: Overlay) {
        val newOverlays = overlays.value.toMutableList()
        newOverlays -= overlay
        overlays.value = newOverlays
    }

    data class FabConfiguration(
        val position: FabPosition,
        val fab: @Composable() () -> Unit
    )

    enum class FabPosition {
        Center, End
    }
}
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
import androidx.ui.core.dp
import androidx.ui.layout.Align
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.layout.Stack
import androidx.ui.material.DrawerState
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.layout.WithModifier

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
    val scaffold = +memo { Scaffold(drawerState) }

    // update state
    scaffold.hasTopAppBar = topAppBar != null
    scaffold.hasDrawer = drawer != null
    scaffold.hasBody = body != null
    scaffold.hasBottomBar = bottomBar != null
    scaffold.hasFab = fabConfiguration != null

    val finalBody: @Composable() () -> Unit = {
        Column {
            if (topAppBar != null) {
                WithModifier(modifier = Inflexible) {
                    topAppBar()
                }
            }

            if (body != null) {
                Container(
                    alignment = Alignment.TopLeft,
                    modifier = Flexible(1f)
                ) {
                    EsSurface {
                        body()
                    }
                }
            }

            if (bottomBar != null) {
                WithModifier(modifier = Inflexible) {
                    bottomBar()
                }
            }
        }

        if (fabConfiguration != null) {
            Align(
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
    }


    ScaffoldAmbient.Provider(value = scaffold) {
        Stack {
            if (drawer != null) {
                expanded {
                    drawer(
                        drawerState.value,
                        { drawerState.value = it },
                        finalBody
                    )
                }
            } else {
                expanded {
                    finalBody()
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
}
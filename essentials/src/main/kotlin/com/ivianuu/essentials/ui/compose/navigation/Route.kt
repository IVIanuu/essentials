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

package com.ivianuu.essentials.ui.compose.navigation

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.ambient

@Immutable
class Route(
    val opaque: Boolean = false,
    val keepState: Boolean = false,
    val transition: RouteTransition? = null,
    val content: @Composable() () -> Unit
) {
    fun copy(
        opaque: Boolean = this.opaque,
        keepState: Boolean = this.keepState,
        transition: RouteTransition? = this.transition,
        content: @Composable() () -> Unit = this.content
    ): Route = Route(
        opaque = opaque,
        keepState = keepState,
        transition = transition,
        content = content
    )
}

internal val RouteAmbient = Ambient.of<Route>()

@Composable
val route: Route
    get() = ambient(RouteAmbient)

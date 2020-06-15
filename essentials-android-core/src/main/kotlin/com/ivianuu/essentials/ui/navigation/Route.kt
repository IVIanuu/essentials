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

package com.ivianuu.essentials.ui.navigation

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.staticAmbientOf
import com.ivianuu.essentials.ui.animatedstack.StackTransition

@Immutable
class Route(
    val enterTransition: StackTransition? = null,
    val exitTransition: StackTransition? = null,
    val opaque: Boolean = false,
    private val content: @Composable () -> Unit
) {

    constructor(
        transition: StackTransition? = null,
        opaque: Boolean = false,
        content: @Composable () -> Unit
    ) : this(transition, transition, opaque, content)

    @Composable
    operator fun invoke() {
        content()
    }

    fun copy(
        enterTransition: StackTransition? = this.enterTransition,
        exitTransition: StackTransition? = this.exitTransition,
        opaque: Boolean = this.opaque,
        content: @Composable () -> Unit = this.content
    ): Route = Route(enterTransition, exitTransition, opaque, content)

}

val RouteAmbient = staticAmbientOf<Route>()

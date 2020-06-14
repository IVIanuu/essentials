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
abstract class Route(
    val enterTransition: StackTransition? = null,
    val exitTransition: StackTransition? = null,
    val opaque: Boolean = false
) {

    constructor(
        transition: StackTransition? = null,
        opaque: Boolean = false
    ) : this(transition, transition, opaque)

    @Composable
    abstract operator fun invoke()

}

fun Route(
    transition: StackTransition? = null,
    opaque: Boolean = false,
    content: @Composable () -> Unit
) = Route(transition, transition, opaque, content)

fun Route(
    enterTransition: StackTransition? = null,
    exitTransition: StackTransition? = null,
    opaque: Boolean = false,
    content: @Composable () -> Unit
): Route = object : Route(enterTransition, exitTransition, opaque) {
    @Composable
    override fun invoke() {
        content()
    }
}

val RouteAmbient = staticAmbientOf<Route>()

fun Route.copy(
    opaque: Boolean = this.opaque,
    enterTransition: StackTransition? = this.enterTransition,
    exitTransition: StackTransition? = this.exitTransition
): Route = object : Route(enterTransition, exitTransition, opaque) {
    override fun invoke() {
        this@copy.invoke()
    }
}

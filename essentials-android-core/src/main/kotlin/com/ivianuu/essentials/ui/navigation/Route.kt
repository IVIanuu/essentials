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

/*
abstract class Route {

    val opaque get() = false

    protected open fun onAttach() {
    }

    protected open fun onDetach() {
    }


}*/

@Immutable
class Route(
    val opaque: Boolean = false,
    val enterTransition: StackTransition? = null,
    val exitTransition: StackTransition? = null,
    val content: @Composable () -> Unit
) {

    constructor(
        opaque: Boolean = false,
        transition: StackTransition? = null,
        content: @Composable () -> Unit
    ) : this(
        opaque = opaque,
        enterTransition = transition,
        exitTransition = transition,
        content = content
    )

    fun copy(
        opaque: Boolean = this.opaque,
        enterTransition: StackTransition? = this.enterTransition,
        exitTransition: StackTransition? = this.exitTransition,
        content: @Composable () -> Unit = this.content
    ): Route = Route(
        opaque = opaque,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        content = content
    )

}

val RouteAmbient =
    staticAmbientOf<Route>()

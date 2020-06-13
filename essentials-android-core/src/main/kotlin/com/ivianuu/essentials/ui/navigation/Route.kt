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
import androidx.compose.Providers
import androidx.compose.staticAmbientOf
import com.ivianuu.essentials.ui.animatedstack.AnimatedStackChild
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.core.RetainedObjects
import com.ivianuu.essentials.ui.core.RetainedObjectsAmbient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

@Immutable
class Route(
    val opaque: Boolean = false,
    val enterTransition: StackTransition? = null,
    val exitTransition: StackTransition? = null,
    val content: @Composable () -> Unit
) {

    internal val stackChild = AnimatedStackChild(
        key = this,
        opaque = opaque,
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) {
        Providers(RetainedObjectsAmbient provides retainedObjects) {
            content()
        }
    }

    private val retainedObjects = RetainedObjects()

    private val _result = CompletableDeferred<Any?>()
    val result: Deferred<Any?> = _result

    constructor(
        opaque: Boolean = false,
        transition: StackTransition? = null,
        content: @Composable () -> Unit
    ) : this(opaque, transition, transition, content)

    fun copy(
        opaque: Boolean = this.opaque,
        enterTransition: StackTransition? = this.enterTransition,
        exitTransition: StackTransition? = this.exitTransition,
        content: @Composable() () -> Unit = this.content
    ): Route = Route(opaque, enterTransition, exitTransition, content)

    internal fun dispose() {
        setResult(null)
        retainedObjects.dispose()
    }

    internal fun setResult(result: Any?) {
        if (!_result.isCompleted) {
            _result.complete(result)
        }
    }

}

val RouteAmbient =
    staticAmbientOf<Route>()

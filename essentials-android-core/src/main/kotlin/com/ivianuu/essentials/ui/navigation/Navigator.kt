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
import androidx.compose.FrameManager
import androidx.compose.Providers
import androidx.compose.Stable
import androidx.compose.frames.modelListOf
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import com.ivianuu.essentials.ui.animatedstack.AnimatedStack
import com.ivianuu.essentials.ui.animatedstack.AnimatedStackEntry
import com.ivianuu.essentials.ui.common.onBackPressed
import com.ivianuu.essentials.ui.core.RetainedObjects
import com.ivianuu.essentials.ui.core.RetainedObjectsAmbient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

@Stable
class Navigator {

    var handleBack by mutableStateOf(true)
    var popsLastRoute by mutableStateOf(false)

    private val _backStack = modelListOf<RouteState>()
    val backStack: List<Route>
        get() = _backStack.map { it.route }

    val hasRoot: Boolean get() = _backStack.isNotEmpty()

    @Composable
    operator fun invoke() {
        val backPressEnabled = handleBack &&
                backStack.isNotEmpty() &&
                (popsLastRoute || backStack.size > 1)
        onBackPressed(enabled = backPressEnabled) {
            if (_backStack.none { it.stackEntry.isAnimating }) popTop()
        }

        Providers(NavigatorAmbient provides this) {
            AnimatedStack(entries = _backStack.map { it.stackEntry })
        }
    }

    fun setRoot(route: Route) {
        setBackStack(listOf(route))
    }

    @JvmName("pushWithoutResult")
    fun push(route: Route) {
        push<Unit>(route)
    }

    fun <T : Any> push(route: Route): Deferred<T?> {
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack)
        return routeState.result as Deferred<T?>
    }

    @JvmName("replaceWithoutResult")
    fun replace(route: Route) {
        replace<Any?>(route)
    }

    fun <T> replace(route: Route): Deferred<T?> {
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack)
        return routeState.result as Deferred<T?>
    }

    fun pop(route: Route, result: Any? = null) {
        val routeState = _backStack.first { it.route == route }
        popInternal(route = routeState, result = result)
    }

    fun popTop(result: Any? = null) {
        val topRoute = _backStack.last()
        popInternal(route = topRoute, result = result)
    }

    fun popToRoot() {
        val newTopRoute = _backStack.first()
        setBackStackInternal(listOf(newTopRoute))
    }

    fun popToRoute(route: Route) {
        val index = _backStack.indexOfFirst { it.route == route }
        val newBackStack = _backStack.subList(0, index)
        setBackStackInternal(newBackStack)
    }

    private fun popInternal(
        route: RouteState,
        result: Any?
    ) {
        val newBackStack = _backStack.toMutableList()
        newBackStack -= route
        route.setResult(result)
        setBackStackInternal(newBackStack)
    }

    fun clear() {
        setBackStack(emptyList())
    }

    fun setBackStack(newBackStack: List<Route>) {
        setBackStackInternal(
            newBackStack.map { route ->
                _backStack.firstOrNull { it.route == route } ?: RouteState(route)
            }
        )
    }

    private fun setBackStackInternal(newBackStack: List<RouteState>) {
        FrameManager.framed {
            synchronized(this) {
                val oldBackStack = _backStack.toList()
                val removedRoutes = oldBackStack.filterNot { it in newBackStack }
                _backStack.clear()
                _backStack += newBackStack
                removedRoutes.forEach {
                    it.setResult(null)
                    it.dispose()
                }
            }
        }
    }

    private inner class RouteState(val route: Route) {

        private val retainedObjects = RetainedObjects()

        private val _result = CompletableDeferred<Any?>()
        val result: Deferred<Any?> = _result

        val stackEntry = AnimatedStackEntry(
            opaque = route.opaque,
            keepState = route.keepState,
            enterTransition = route.enterTransition,
            exitTransition = route.exitTransition,
            content = {
                Providers(
                    RetainedObjectsAmbient provides retainedObjects,
                    RouteAmbient provides route,
                    children = route.content
                )
            }
        )

        fun dispose() {
            retainedObjects.dispose()
        }

        fun setResult(result: Any?) {
            if (!_result.isCompleted) {
                _result.complete(result)
            }
        }
    }
}

val NavigatorAmbient =
    staticAmbientOf<Navigator>()

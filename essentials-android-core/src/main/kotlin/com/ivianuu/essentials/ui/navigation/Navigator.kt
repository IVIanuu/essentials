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
import androidx.compose.Providers
import androidx.compose.Stable
import androidx.compose.frames.modelListOf
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import com.ivianuu.essentials.ui.animatedstack.AnimatedStack
import com.ivianuu.essentials.ui.animatedstack.AnimatedStackEntry
import com.ivianuu.essentials.ui.common.onBackPressed
import com.ivianuu.essentials.ui.core.RetainedObjects
import com.ivianuu.essentials.ui.core.RetainedObjectsAmbient
import com.ivianuu.essentials.ui.coroutines.compositionCoroutineScope
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// todo remove InjectedNavigator

@Composable
fun InjectedNavigator(
    startRoute: Route,
    handleBack: Boolean = true,
    popsLastRoute: Boolean = false
): Navigator {
    val navigator = inject<Navigator>()

    navigator.handleBack = handleBack
    navigator.popsLastRoute = popsLastRoute
    if (!navigator.hasRoot) {
        navigator.setRoot(startRoute)
    }

    return navigator
}

@Composable
fun Navigator(
    startRoute: Route? = null,
    handleBack: Boolean = true,
    popsLastRoute: Boolean = false
): Navigator {
    val coroutineScope = compositionCoroutineScope()
    val dispatchers = inject<AppCoroutineDispatchers>()
    val navigator = remember {
        Navigator(
            coroutineScope = coroutineScope,
            dispatchers = dispatchers
        )
    }

    if (navigator.handleBack != handleBack) navigator.handleBack = handleBack
    if (navigator.popsLastRoute != popsLastRoute) navigator.popsLastRoute = popsLastRoute

    if (startRoute != null && !navigator.hasRoot) {
        navigator.setRoot(startRoute)
    }

    return navigator
}

// todo remove main thread requirement
// todo main thread requirement comes from compose
@Stable
class Navigator(
    private val coroutineScope: CoroutineScope,
    private val dispatchers: AppCoroutineDispatchers
) {

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
        @Suppress("DeferredResultUnused")
        coroutineScope.launch(dispatchers.main) { push<Any?>(route) }
    }

    suspend fun <T> push(route: Route): T? = withContext(dispatchers.main) {
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack)
        return@withContext routeState.awaitResult<T>()
    }

    @JvmName("replaceWithoutResult")
    fun replace(route: Route) {
        @Suppress("DeferredResultUnused")
        coroutineScope.launch(dispatchers.main) { replace<Any?>(route) }
    }

    suspend fun <T> replace(route: Route): T? = withContext(dispatchers.main) {
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack)
        return@withContext routeState.awaitResult()
    }

    fun pop(route: Route, result: Any? = null) {
        coroutineScope.launch(dispatchers.main) {
            val routeState = _backStack.first { it.route == route }
            popInternal(route = routeState, result = result)
        }
    }

    fun popTop(result: Any? = null) {
        coroutineScope.launch(dispatchers.main) {
            val topRoute = _backStack.last()
            popInternal(route = topRoute, result = result)
        }
    }

    fun popToRoot() {
        coroutineScope.launch(dispatchers.main) {
            val newTopRoute = _backStack.first()
            setBackStackInternal(listOf(newTopRoute))
        }
    }

    fun popToRoute(route: Route) {
        coroutineScope.launch(dispatchers.main) {
            val index = _backStack.indexOfFirst { it.route == route }
            val newBackStack = _backStack.subList(0, index)
            setBackStackInternal(newBackStack)
        }
    }

    private suspend fun popInternal(
        route: RouteState,
        result: Any?
    ) = withContext(dispatchers.main) {
        val newBackStack = _backStack.toMutableList()
        newBackStack -= route
        route.setResult(result)
        setBackStackInternal(newBackStack)
    }

    fun clear() {
        coroutineScope.launch(dispatchers.main) { setBackStack(emptyList()) }
    }

    fun setBackStack(newBackStack: List<Route>) {
        coroutineScope.launch(dispatchers.main) {
            setBackStackSuspend(newBackStack)
        }
    }

    suspend fun setBackStackSuspend(newBackStack: List<Route>) {
        setBackStackInternal(
            newBackStack.map { route ->
                _backStack.firstOrNull { it.route == route } ?: RouteState(route)
            }
        )
    }

    private suspend fun setBackStackInternal(newBackStack: List<RouteState>) =
        withContext(dispatchers.main) {
            val oldBackStack = _backStack.toList()
            val removedRoutes = newBackStack.filterNot { it in oldBackStack }
            _backStack.clear()
            _backStack += newBackStack
            removedRoutes.forEach {
                it.setResult(null)
                it.dispose()
            }
        }

    suspend fun <T> awaitResult(route: Route): T? =
        _backStack.first { it.route == route }.awaitResult()

    private inner class RouteState(val route: Route) {

        private val retainedObjects = RetainedObjects()
        private val result = CompletableDeferred<Any?>()

        val stackEntry = AnimatedStackEntry(
            opaque = route.opaque,
            keepState = route.keepState,
            enterAnimation = route.enterAnimation,
            exitAnimation = route.exitAnimation,
            content = {
                Providers(
                    RetainedObjectsAmbient provides retainedObjects,
                    RouteAmbient provides route
                ) {
                    route.content()
                }
            }
        )

        fun dispose() {
            retainedObjects.dispose()
        }

        suspend fun <T> awaitResult(): T? = result.await() as? T

        fun setResult(result: Any?) {
            if (!this.result.isCompleted) {
                this.result.complete(result)
            }
        }
    }
}

val NavigatorAmbient =
    staticAmbientOf<Navigator>()

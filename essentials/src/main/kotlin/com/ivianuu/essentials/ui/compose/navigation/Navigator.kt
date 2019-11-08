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
import androidx.compose.Observe
import androidx.compose.Recompose
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.onBackPressed
import com.ivianuu.essentials.ui.compose.common.retainedState
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.coroutines.coroutineScope
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.util.AppDispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// todo transition support

@Composable
fun Navigator(
    handleBack: Boolean = true,
    key: String? = null,
    startRoute: () -> Route
) = composable("Navigator") {
    Observe {
        Recompose { recompose ->
            val navigatorState = +retainedState("Navigator${key.orEmpty()}") { NavigatorState() }
            val coroutineScope = +coroutineScope()
            val appDispatchers = +inject<AppDispatchers>()
            val navigator = +memo {
                Navigator(coroutineScope, appDispatchers, navigatorState.value, startRoute())
            }

            navigator.recompose = recompose

            if (handleBack && navigator.backStack.size > 1) {
                composable("onBackPressed") {
                    +onBackPressed { navigator.pop() }
                }
            }

            NavigatorAmbient.Provider(navigator) {
                navigator.compose()
            }
        }
    }
}

class Navigator internal constructor(
    private val coroutineScope: CoroutineScope,
    private val dispatchers: AppDispatchers,
    private val state: NavigatorState,
    startRoute: Route
) {

    val backStack: List<Route>
        get() = state.backStack

    internal lateinit var recompose: () -> Unit

    private val resultsByRoute = mutableMapOf<Route, CompletableDeferred<Any?>>()

    init {
        if (state.backStack.isEmpty()) {
            state.backStack += startRoute
        }
    }

    @JvmName("pushWithoutResult")
    fun push(route: Route) {
        @Suppress("DeferredResultUnused")
        coroutineScope.launch { push<Any?>(route) }
    }

    suspend fun <T> push(route: Route): T? {
        d { "push $route" }
        val newBackStack = backStack.toMutableList()
        newBackStack += route
        setBackStack(newBackStack)
        val deferredResult = CompletableDeferred<Any?>()
        synchronized(resultsByRoute) { resultsByRoute[route] = deferredResult }
        return deferredResult.await() as? T
    }

    fun pop(result: Any? = null) {
        coroutineScope.launch { popInternal(result) }
    }

    private suspend fun popInternal(result: Any?) {
        d { "pop result $result" }
        val newBackStack = backStack.toMutableList()
        val removedRoute = newBackStack.removeAt(backStack.lastIndex)
        setBackStack(newBackStack)
        val deferredResult = synchronized(resultsByRoute) { resultsByRoute.remove(removedRoute) }
        deferredResult?.complete(result)
    }

    @JvmName("replaceWithoutResult")
    fun replace(route: Route) {
        @Suppress("DeferredResultUnused")
        coroutineScope.launch { replace<Any?>(route) }
    }

    suspend fun <T> replace(route: Route): T? {
        d { "replace $route" }
        val newBackStack = backStack.toMutableList()
        if (newBackStack.isNotEmpty()) {
            newBackStack.removeAt(newBackStack.lastIndex)
        }
        newBackStack += route
        setBackStack(newBackStack)
        val deferredResult = CompletableDeferred<Any?>()
        synchronized(resultsByRoute) { resultsByRoute[route] = deferredResult }
        return deferredResult.await() as? T
    }

    @Composable
    internal fun compose() = composable("NavigatorContent") {
        NavigatorLayout {
            backStack
                .filter { it.isVisible() || it.keepState }
                .forEach { route ->
                    composable(route.key) {
                        Observe {
                            RouteAmbient.Provider(route) {
                                ParentData(NavigatorParentData(route.isVisible())) {
                                    route.compose()
                                }
                            }
                        }
                    }
            }
        }
    }

    private suspend fun setBackStack(newBackStack: List<Route>) {
        withContext(dispatchers.main) {
            state.backStack = newBackStack
            recompose()
        }
    }

    private fun Route.isVisible(): Boolean = this in getVisibleRoutes()

    private fun getVisibleRoutes(): List<Route> {
        val visibleRoutes = mutableListOf<Route>()

        for (route in state.backStack.reversed()) {
            visibleRoutes += route
            if (!route.isFloating) break
        }

        return visibleRoutes
    }
}

val NavigatorAmbient = Ambient.of<Navigator>()

interface Route {
    val key: Any
    val keepState: Boolean
    val isFloating: Boolean
    @Composable
    fun compose()
}

val RouteAmbient = Ambient.of<Route>()

fun Route(
    key: Any,
    keepState: Boolean = false,
    isFloating: Boolean = false,
    compose: @Composable() () -> Unit
): Route = object : Route {
    override val key: Any
        get() = key
    override val keepState: Boolean
        get() = keepState
    override val isFloating: Boolean
        get() = isFloating

    override fun compose() {
        compose.invoke()
    }
}

internal data class NavigatorState(
    var backStack: List<Route> = emptyList()
)

@Composable
private fun NavigatorLayout(
    children: @Composable() () -> Unit
) = composable("NavigatorLayout") {
    Layout(children) { measureables, constraints ->
        // force children to fill the whole space
        val childConstraints = constraints.copy(
            minWidth = constraints.maxWidth,
            minHeight = constraints.maxHeight
        )

        // get only visible routes
        val placeables = measureables
            .filter { (it.parentData as NavigatorParentData).isVisible }
            .map { it.measure(childConstraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { it.place(IntPx.Zero, IntPx.Zero) }
        }
    }
}

private data class NavigatorParentData(val isVisible: Boolean)
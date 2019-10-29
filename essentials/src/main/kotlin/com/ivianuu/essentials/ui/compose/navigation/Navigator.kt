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
import androidx.compose.Recompose
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.layout.Stack
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.handleBack
import com.ivianuu.essentials.ui.compose.common.retainedState
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.coroutines.coroutineScope
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.util.AppDispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Navigator(
    handleBack: Boolean = true,
    key: String? = null,
    startRoute: () -> Route
) = composable("Navigator") {
    Recompose { recompose ->
        val navigatorState = +retainedState("Navigator${key.orEmpty()}") { NavigatorState() }
        val coroutineScope = +coroutineScope()
        val appDispatchers = +inject<AppDispatchers>()
        val navigator = +memo {
            Navigator(coroutineScope, appDispatchers, navigatorState.value, startRoute())
        }

        navigator.recompose = recompose

        d { "navigator fun $handleBack ${navigator.backStack.size}" }

        if (handleBack && navigator.backStack.size > 1) {
            composable("handleBack") {
                +handleBack { navigator.pop() }
            }
        }

        NavigatorAmbient.Provider(navigator) {
            navigator.compose()
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
    fun compose() = composable("NavigatorContent") {
        Stack {
            backStack.takeLast(1).forEach { route ->
                composable(route.key) {
                    route.compose()
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
}

val NavigatorAmbient = Ambient.of<Navigator>()

interface Route {
    val key: Any
    @Composable
    fun compose()
}

fun Route(key: Any, compose: @Composable() () -> Unit): Route = object : Route {
    override val key: Any
        get() = key

    override fun compose() {
        compose.invoke()
    }
}

internal data class NavigatorState(
    var backStack: List<Route> = emptyList()
)
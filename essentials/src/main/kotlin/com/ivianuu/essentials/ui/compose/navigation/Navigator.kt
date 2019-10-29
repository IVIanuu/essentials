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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun Navigator(
    handleBack: Boolean = true,
    key: String? = null,
    startRoute: () -> Route
) = composable("Navigator") {
    Recompose { recompose ->
        val navigatorState = +retainedState("Navigator${key.orEmpty()}") { NavigatorState() }
        val navigator = +memo { Navigator(navigatorState.value, startRoute()) }
        navigator.recompose = recompose

        if (handleBack && navigator.backStack.size > 1) {
            +memo(navigator.backStack.size) {
                +handleBack { navigator.pop() }
            }
        }

        NavigatorAmbient.Provider(navigator) {
            navigator.compose()
        }
    }
}

class Navigator internal constructor(
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
        GlobalScope.launch { push<Any?>(route) }
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
        GlobalScope.launch { replace<Any?>(route) }
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
            backStack.forEach { route ->
                composable(route.key) {
                    route.compose()
                }
            }
        }
    }

    private fun setBackStack(newBackStack: List<Route>) {
        synchronized(state) {
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
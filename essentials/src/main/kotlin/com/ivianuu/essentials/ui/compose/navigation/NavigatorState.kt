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
import androidx.compose.ambient
import androidx.compose.frames.modelListOf
import androidx.compose.onCommit
import androidx.compose.remember
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.Overlay
import com.ivianuu.essentials.ui.compose.common.OverlayEntry
import com.ivianuu.essentials.ui.compose.common.OverlayState
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.common.onBackPressed
import com.ivianuu.essentials.ui.compose.coroutines.coroutineScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Navigator(
    startRoute: Route,
    handleBack: Boolean = true
) {
    val coroutineScope = coroutineScope()
    val overlayState = remember { OverlayState() }
    val navigatorState = remember {
        NavigatorState(
            overlayState = overlayState,
            coroutineScope = coroutineScope,
            startRoute = startRoute,
            handleBack = handleBack
        )
    }

    remember(handleBack) { navigatorState.handleBack = handleBack }

    Navigator(state = navigatorState)
}

@Composable
fun Navigator(state: NavigatorState) {
    NavigatorAmbient.Provider(value = state) {
        if (state.handleBack && state.backStack.size > 1) {
            onBackPressed { state.pop() }
        }
        Overlay(state = state.overlayState)
    }
}

class NavigatorState internal constructor(
    internal val overlayState: OverlayState,
    private val coroutineScope: CoroutineScope,
    startRoute: Route,
    handleBack: Boolean
) {

    var handleBack by framed(handleBack)

    private val _backStack = modelListOf<RouteState>()
    val backStack: List<Route>
        get() = _backStack.map { it.route }

    init {
        if (_backStack.isEmpty()) {
            push(startRoute)
        }
    }

    @JvmName("pushWithoutResult")
    fun push(route: Route) {
        @Suppress("DeferredResultUnused")
        coroutineScope.launch { push<Any?>(route) }
    }

    suspend fun <T> push(route: Route): T? {
        d { "push $route" }

        val routeState = RouteState(route)
        _backStack += routeState
        routeState.attach()
        return routeState.awaitResult()
    }

    fun pop(result: Any? = null) {
        coroutineScope.launch { popInternal(result) }
    }

    private suspend fun popInternal(result: Any?) {
        d { "pop result $result" }
        val removedRoute = _backStack.removeAt(backStack.lastIndex)
        removedRoute.detach()
        removedRoute.setResult(result)
    }

    @JvmName("replaceWithoutResult")
    fun replace(route: Route) {
        @Suppress("DeferredResultUnused")
        coroutineScope.launch { replace<Any?>(route) }
    }

    suspend fun <T> replace(route: Route): T? {
        d { "replace $route" }

        if (_backStack.isNotEmpty()) {
            val removedRoute = _backStack.removeAt(_backStack.lastIndex)
            removedRoute.detach()
        }

        val routeState = RouteState(route)
        _backStack += routeState
        routeState.attach()
        return routeState.awaitResult()
    }

    private inner class RouteState(val route: Route) {

        private val result = CompletableDeferred<Any?>()

        private val overlayEntry = OverlayEntry(
            opaque = true,
            keepState = route.keepState,
            content = {
                if (transitionState == RouteTransitionState.Init) {
                    onCommit {
                        transitionState = RouteTransitionState.Pushed
                    }
                }
                route.transition(
                    transitionState,
                    onTransitionComplete,
                    route.content
                )
            }
        )

        private val onTransitionComplete: (RouteTransitionState) -> Unit = { completedState ->
            notifiedTransitionState = transitionState
            if (completedState == RouteTransitionState.Popped) {
                overlayState.remove(overlayEntry)
            }
        }

        private var transitionState by framed(RouteTransitionState.Init)
        private var notifiedTransitionState = RouteTransitionState.Init

        fun attach() {
            overlayState.add(overlayEntry)
        }

        fun detach() {
            transitionState = RouteTransitionState.Popped
        }

        suspend fun <T> awaitResult(): T? = result.await() as? T

        fun setResult(result: Any?) {
            this.result.complete(result)
        }
    }
}

private val NavigatorAmbient = Ambient.of<NavigatorState>()

@Composable
val navigator: NavigatorState
    get() = ambient(NavigatorAmbient)
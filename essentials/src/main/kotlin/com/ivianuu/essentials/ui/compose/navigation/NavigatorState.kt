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
import androidx.compose.onActive
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

    var types: List<RouteTransition.Type> by framed(
        listOf(OpacityRouteTransitionType, CanvasRouteTransitionType)
    )

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

        val prev = _backStack.lastOrNull()
        val routeState = RouteState(route)
        _backStack += routeState
        prev?.exit(routeState, true)
        routeState.enter(prev, true)

        return routeState.awaitResult()
    }

    fun pop(result: Any? = null) {
        coroutineScope.launch { popInternal(result) }
    }

    private suspend fun popInternal(result: Any?) {
        d { "pop result $result" }
        val removedRoute = _backStack.removeAt(backStack.lastIndex)
        val newTopRoute = _backStack.lastOrNull()
        removedRoute.exit(newTopRoute, false)
        newTopRoute?.enter(removedRoute, false)
        removedRoute.setResult(result)
    }

    @JvmName("replaceWithoutResult")
    fun replace(route: Route) {
        @Suppress("DeferredResultUnused")
        coroutineScope.launch { replace<Any?>(route) }
    }

    suspend fun <T> replace(route: Route): T? {
        d { "replace $route" }

        val routeState = RouteState(route)
        _backStack += routeState

        val removedRoute = _backStack.lastOrNull()
        if (removedRoute != null) _backStack -= removedRoute
        removedRoute?.exit(routeState, true)
        routeState.enter(removedRoute, true)

        return routeState.awaitResult()
    }

    private inner class RouteState(val route: Route) {

        private val result = CompletableDeferred<Any?>()

        private val overlayEntry = OverlayEntry(
            opaque = true,
            keepState = route.keepState,
            content = {
                onActive {
                    d { "${route.name} -> lifecycle on active" }
                    onDispose {
                        d { "${route.name} -> lifecycle on dispose" }
                    }
                }

                RouteTransitionWrapper(
                    route = route,
                    transition = transition ?: DefaultRouteTransition,
                    state = transitionState,
                    lastState = lastTransitionState,
                    onTransitionComplete = onTransitionComplete,
                    types = types,
                    children = route.content
                )
            }
        )

        private val onTransitionComplete: (RouteTransition.State) -> Unit = { completedState ->
            if (completedState == RouteTransition.State.ExitFromPush) {
                other?.let {
                    d { "${route.name} notify complete to other ${other?.route?.name}" }
                }
                other?.onOtherTransitionComplete(this)
                other = null
            }

            if (completedState == RouteTransition.State.ExitFromPop) {
                overlayState.remove(overlayEntry)
            }
        }

        private var transition by framed(route.transition)
        private var transitionState by framed(RouteTransition.State.Init)
        private var lastTransitionState by framed(RouteTransition.State.Init)

        private var other: RouteState? = null

        private fun onOtherTransitionComplete(other: RouteState) {
            d { "${route.name} on other ${other.route.name} transition complete" }
            overlayEntry.opaque = route.opaque
        }

        fun enter(prev: RouteState?, isPush: Boolean) {
            overlayEntry.opaque = route.opaque || isPush
            if (isPush) overlayState.add(overlayEntry)
            lastTransitionState = transitionState
            transitionState =
                if (isPush) RouteTransition.State.EnterFromPush else RouteTransition.State.EnterFromPop
            transition = if (isPush) route.transition else prev!!.route.transition
            d { "${route.name} enter -> prev ${prev?.route?.name} is push $isPush state $transitionState last state $lastTransitionState opaque ${overlayEntry.opaque}" }
        }

        fun exit(next: RouteState?, isPush: Boolean) {
            overlayEntry.opaque = route.opaque || !isPush
            if (isPush) other = next
            lastTransitionState = transitionState
            transitionState =
                if (isPush) RouteTransition.State.ExitFromPush else RouteTransition.State.ExitFromPop
            transition = if (isPush) next!!.route.transition else route.transition
            d { "${route.name} exit -> next ${next?.route?.name} is push $isPush state $transitionState last state $lastTransitionState opaque ${overlayEntry.opaque}" }
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
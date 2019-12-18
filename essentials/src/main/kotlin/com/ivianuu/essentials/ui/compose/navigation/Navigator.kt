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
import androidx.compose.ambient
import androidx.compose.frames.modelListOf
import androidx.compose.remember
import androidx.ui.core.CoroutineContextAmbient
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.AbsorbPointer
import com.ivianuu.essentials.ui.compose.common.Overlay
import com.ivianuu.essentials.ui.compose.common.OverlayEntry
import com.ivianuu.essentials.ui.compose.common.OverlayState
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.common.onBackPressed
import com.ivianuu.essentials.ui.compose.common.retained
import com.ivianuu.essentials.ui.compose.core.Stable
import com.ivianuu.essentials.ui.compose.coroutines.coroutineContext
import com.ivianuu.essentials.ui.compose.coroutines.retainedCoroutineScope
import com.ivianuu.essentials.util.sourceLocation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Navigator(
    startRoute: Route,
    handleBack: Boolean = true
) {
    Navigator(state = RetainedNavigatorState(startRoute = startRoute, handleBack = handleBack))
}

@Composable
fun Navigator(state: NavigatorState) {
    NavigatorAmbient.Provider(value = state) {
        Observe {
            onBackPressed(enabled = state.handleBack && state.backStack.size > 1) {
                if (state.runningTransitions == 0) state.pop()
            }
        }
        d { "invoke navigator" }
        Overlay(state = state.overlayState)
    }
}

@Composable
fun RetainedNavigatorState(
    key: Any = sourceLocation(),
    startRoute: Route? = null,
    overlayState: OverlayState = remember { OverlayState() },
    coroutineScope: CoroutineScope = retainedCoroutineScope("Scope:$key"),
    handleBack: Boolean = true
): NavigatorState = retained(key = "Navigator:$key") {
    NavigatorState(
        coroutineScope = coroutineScope,
        overlayState = overlayState,
        startRoute = startRoute,
        handleBack = handleBack
    )
}

@Stable
class NavigatorState(
    private val coroutineScope: CoroutineScope,
    internal val overlayState: OverlayState = OverlayState(),
    startRoute: Route? = null,
    handleBack: Boolean = true
) {

    var handleBack by framed(handleBack)
    internal var runningTransitions by framed(0)

    private val _backStack = modelListOf<RouteState>()
    val backStack: List<Route>
        get() = _backStack.map { it.route }

    var types: List<RouteTransition.Type> by framed(
        listOf(OpacityRouteTransitionType, CanvasRouteTransitionType)
    )

    init {
        if (_backStack.isEmpty() && startRoute != null) {
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
        if (prev !in _backStack.filterVisible()) prev?.exit(routeState, true)
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
        if (!removedRoute.route.opaque) newTopRoute?.enter(removedRoute, false)
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

    private fun List<RouteState>.filterVisible(): List<RouteState> {
        val visibleRoutes = mutableListOf<RouteState>()

        for (routeState in reversed()) {
            visibleRoutes += routeState
            if (!routeState.route.opaque) break
        }

        return visibleRoutes
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NavigatorState

        if (overlayState != other.overlayState) return false
        if (_backStack != other._backStack) return false
        if (handleBack != other.handleBack) return false
        if (runningTransitions != other.runningTransitions) return false
        if (types != other.types) return false

        return true
    }

    override fun hashCode(): Int {
        var result = overlayState.hashCode()
        result = 31 * result + _backStack.hashCode()
        result = 31 * result + handleBack.hashCode()
        result = 31 * result + runningTransitions.hashCode()
        result = 31 * result + types.hashCode()
        return result
    }

    private inner class RouteState(val route: Route) {

        private val result = CompletableDeferred<Any?>()

        private val overlayEntry = OverlayEntry(
            opaque = route.opaque,
            keepState = route.keepState,
            content = {
                val coroutineContext = coroutineContext()
                CoroutineContextAmbient.Provider(coroutineContext) {
                    AbsorbPointer(absorb = absorbTouches) {
                        RouteTransitionWrapper(
                            transition = transition ?: ambient(DefaultRouteTransitionAmbient),
                            state = transitionState,
                            lastState = lastTransitionState,
                            onTransitionComplete = onTransitionComplete,
                            types = types,
                            children = {
                                RouteAmbient.Provider(
                                    value = route,
                                    children = route.content
                                )
                            }
                        )
                    }
                }
            }
        )

        private val onTransitionComplete: (RouteTransition.State) -> Unit = { completedState ->
            if (completedState != RouteTransition.State.Init) {
                absorbTouches = false
                runningTransitions--
            }
            lastTransitionState = completedState
            if (completedState == RouteTransition.State.ExitFromPush) {
                other?.onOtherTransitionComplete()
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
        private var absorbTouches by framed(false)

        private fun onOtherTransitionComplete() {
            overlayEntry.opaque = route.opaque
        }

        fun enter(prev: RouteState?, isPush: Boolean) {
            overlayEntry.opaque = route.opaque || isPush
            absorbTouches = true
            runningTransitions++
            if (isPush) overlayState.add(overlayEntry)
            lastTransitionState = transitionState
            transitionState =
                if (isPush) RouteTransition.State.EnterFromPush else RouteTransition.State.EnterFromPop
            transition = if (isPush) route.transition else prev!!.route.transition
        }

        fun exit(next: RouteState?, isPush: Boolean) {
            absorbTouches = true
            runningTransitions++
            overlayEntry.opaque = route.opaque || !isPush
            if (isPush) other = next
            lastTransitionState = transitionState
            transitionState =
                if (isPush) RouteTransition.State.ExitFromPush else RouteTransition.State.ExitFromPop
            transition = if (isPush) next!!.route.transition else route.transition
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

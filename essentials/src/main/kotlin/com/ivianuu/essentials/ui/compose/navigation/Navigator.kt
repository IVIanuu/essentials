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
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack, true, null)
        return routeState.awaitResult()
    }

    fun pop(result: Any? = null) {
        coroutineScope.launch { popInternal(result) }
    }

    private suspend fun popInternal(result: Any?) {
        d { "pop result $result" }
        val newBackStack = _backStack.toMutableList()
        val removedRoute = newBackStack.removeAt(newBackStack.lastIndex)
        setBackStackInternal(newBackStack, false, null)
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
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack, true, null)
        return routeState.awaitResult()
    }

    fun setBackStack(
        newBackStack: List<Route>,
        isPush: Boolean,
        transition: RouteTransition? = null
    ) {
        coroutineScope.launch {
            setBackStackSuspend(newBackStack, isPush, transition)
        }
    }

    suspend fun setBackStackSuspend(
        newBackStack: List<Route>,
        isPush: Boolean,
        transition: RouteTransition? = null
    ) {
        setBackStackInternal(
            newBackStack.map {
                _backStack.firstOrNull { it.route == route } ?: RouteState(it)
            },
            isPush,
            transition
        )
    }

    suspend fun <T> awaitResult(route: Route): T? =
        _backStack.firstOrNull { it.route == route }?.awaitResult()

    private suspend fun setBackStackInternal(
        newBackStack: List<RouteState>,
        isPush: Boolean,
        transition: RouteTransition?
    ) {
        if (newBackStack == _backStack) return

        // do not allow pushing the same route twice
        newBackStack
            .groupBy { it }
            .forEach {
                check(it.value.size == 1) {
                    "Trying to push the same route to the backStack more than once. $it"
                }
            }

        val oldBackStack = _backStack.toList()
        val oldVisibleRoutes = oldBackStack.filterVisible()

        val removedRoutes = oldBackStack.filterNot { it in newBackStack }

        _backStack.clear()
        _backStack += newBackStack

        val newVisibleRoutes = newBackStack.filterVisible()

        if (oldVisibleRoutes != newVisibleRoutes) {
            val oldTopRoute = oldVisibleRoutes.lastOrNull()
            val newTopRoute = newVisibleRoutes.lastOrNull()

            // check if we should animate the top routes
            val replacingTopRoutes = newTopRoute != null && (oldTopRoute == null
                    || oldTopRoute != newTopRoute)

            // Remove all visible routes which shouldn't be visible anymore
            // from top to bottom
            oldVisibleRoutes
                .dropLast(if (replacingTopRoutes) 1 else 0)
                .reversed()
                .filterNot { it in newVisibleRoutes }
                .forEach { route ->
                    val localTransition = transition
                        ?: route.route.exitTransition

                    performChange(
                        from = route,
                        to = null,
                        isPush = isPush,
                        transition = localTransition
                    )
                }

            // Add any new routes to the backStack from bottom to top
            newVisibleRoutes
                .dropLast(if (replacingTopRoutes) 1 else 0)
                .filterNot { it in oldVisibleRoutes }
                .forEachIndexed { i, route ->
                    val localTransition = transition ?: route.route.enterTransition
                    performChange(
                        from = newVisibleRoutes.getOrNull(i - 1),
                        to = route,
                        isPush = true,
                        transition = localTransition
                    )
                }

            // Replace the old visible top with the new one
            if (replacingTopRoutes) {
                val localTransition = transition
                    ?: (if (isPush) newTopRoute?.route?.enterTransition
                    else oldTopRoute?.route?.exitTransition)
                performChange(
                    from = oldTopRoute,
                    to = newTopRoute,
                    isPush = isPush,
                    transition = localTransition
                )
            }
        }

        removedRoutes.forEach { it.setResult(null) }
    }

    private fun performChange(
        from: RouteState?,
        to: RouteState?,
        isPush: Boolean,
        transition: RouteTransition?
    ) {
        val exitFrom = from != null && (to == null || !to.route.opaque)
        if (exitFrom) from!!.exit(to = to, isPush = isPush, transition = transition)
        to?.enter(from = from, isPush = isPush, transition = transition)
    }
    
    private fun List<RouteState>.filterVisible(): List<RouteState> {
        val visibleRoutes = mutableListOf<RouteState>()

        for (routeState in reversed()) {
            visibleRoutes += routeState
            if (!routeState.route.opaque) break
        }

        return visibleRoutes
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

        private var transition by framed(route.enterTransition)
        private var transitionState by framed(RouteTransition.State.Init)
        private var lastTransitionState by framed(RouteTransition.State.Init)

        private var other: RouteState? = null
        private var absorbTouches by framed(false)

        private fun onOtherTransitionComplete() {
            overlayEntry.opaque = route.opaque
        }

        fun enter(
            from: RouteState?,
            isPush: Boolean,
            transition: RouteTransition?
        ) {
            overlayEntry.opaque = route.opaque || isPush
            absorbTouches = true
            runningTransitions++
            if (isPush) overlayState.add(overlayEntry)
            lastTransitionState = transitionState
            transitionState =
                if (isPush) RouteTransition.State.EnterFromPush else RouteTransition.State.EnterFromPop
            this.transition = transition
        }

        fun exit(
            to: RouteState?,
            isPush: Boolean,
            transition: RouteTransition?
        ) {
            absorbTouches = true
            runningTransitions++
            overlayEntry.opaque = route.opaque || !isPush
            if (isPush) other = to
            lastTransitionState = transitionState
            transitionState =
                if (isPush) RouteTransition.State.ExitFromPush else RouteTransition.State.ExitFromPop
            this.transition = transition
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

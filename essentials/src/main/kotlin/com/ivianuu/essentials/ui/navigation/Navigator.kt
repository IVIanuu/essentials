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

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.Observe
import androidx.compose.Stable
import androidx.compose.ambient
import androidx.compose.frames.modelListOf
import androidx.compose.onDispose
import androidx.compose.remember
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.common.AbsorbPointer
import com.ivianuu.essentials.ui.common.Overlay
import com.ivianuu.essentials.ui.common.OverlayEntry
import com.ivianuu.essentials.ui.common.OverlayState
import com.ivianuu.essentials.ui.common.framed
import com.ivianuu.essentials.ui.common.onBackPressed
import com.ivianuu.essentials.ui.core.RetainedObjects
import com.ivianuu.essentials.ui.core.RetainedObjectsAmbient
import com.ivianuu.essentials.ui.coroutines.ProvideCoroutineScope
import com.ivianuu.essentials.ui.coroutines.coroutineScope
import com.ivianuu.essentials.ui.injekt.inject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// todo remove InjectedNavigator

@Composable
fun InjectedNavigator(
    startRoute: Route,
    handleBack: Boolean = true,
    popsLastRoute: Boolean = false
) {
    val state = inject<NavigatorState>()

    state.handleBack = handleBack
    state.popsLastRoute = popsLastRoute

    if (!state.hasRoot) {
        state.setRoot(startRoute)
    }

    Navigator(state = state)
}

@Composable
fun Navigator(
    startRoute: Route,
    handleBack: Boolean = true,
    popsLastRoute: Boolean = false
) {
    val coroutineScope = coroutineScope
    val state = remember {
        NavigatorState(
            startRoute = startRoute,
            handleBack = handleBack,
            coroutineScope = coroutineScope
        )
    }

    state.handleBack = handleBack
    state.popsLastRoute = popsLastRoute

    Navigator(state = state)
}

@Composable
fun Navigator(state: NavigatorState) {
    state.defaultRouteTransition = ambient(DefaultRouteTransitionAmbient)
    NavigatorAmbient.Provider(value = state) {
        Observe {
            val enabled = state.handleBack &&
                    state.backStack.isNotEmpty() &&
                    (state.popsLastRoute || state.backStack.size > 1)
            d { "back press enabled $enabled" }
            onBackPressed(enabled = enabled) {
                d { "on back pressed ${state.runningTransitions}" }
                if (state.runningTransitions == 0) state.popTop()
            }
        }

        Overlay(state = state.overlayState)
    }

    onDispose { state.dispose() }
}

// todo remove main thread requirement
@Stable
class NavigatorState(
    private val coroutineScope: CoroutineScope,
    internal val overlayState: OverlayState = OverlayState(),
    startRoute: Route? = null,
    handleBack: Boolean = true,
    popsLastRoute: Boolean = false
) {

    var handleBack by framed(handleBack)
    var popsLastRoute by framed(popsLastRoute)
    internal var runningTransitions by framed(0)

    private val _backStack = modelListOf<RouteState>()
    val backStack: List<Route>
        get() = _backStack.map { it.route }

    val hasRoot: Boolean get() = _backStack.isNotEmpty()

    var routeTransitionTypes: List<RouteTransition.Type> by framed(
        listOf(OpacityRouteTransitionType, CanvasRouteTransitionType, ModifierRouteTransitionType)
    )

    internal var defaultRouteTransition = DefaultRouteTransition

    init {
        if (!hasRoot && startRoute != null) {
            setRoot(startRoute)
        }
    }

    fun setRoot(route: Route) {
        setBackStack(listOf(route), true)
    }

    @JvmName("pushWithoutResult")
    fun push(route: Route) {
        @Suppress("DeferredResultUnused")
        coroutineScope.launch(Dispatchers.Main) { push<Any?>(route) }
    }

    suspend fun <T> push(route: Route): T? = withContext(Dispatchers.Main) {
        d { "push $route" }
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack, true, null)
        return@withContext routeState.awaitResult<T>()
            .also {
                d { "on push result for $route -> $it" }
            }
    }

    @JvmName("replaceWithoutResult")
    fun replace(route: Route) {
        @Suppress("DeferredResultUnused")
        coroutineScope.launch(Dispatchers.Main) { replace<Any?>(route) }
    }

    suspend fun <T> replace(route: Route): T? = withContext(Dispatchers.Main) {
        d { "replace $route" }

        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack, true, null)
        return@withContext routeState.awaitResult()
    }

    fun pop(route: Route, result: Any? = null) {
        coroutineScope.launch(Dispatchers.Main) {
            val routeState = _backStack.first { it.route == route }
            popInternal(route = routeState, result = result)
        }
    }

    fun popTop(result: Any? = null) {
        coroutineScope.launch(Dispatchers.Main) {
            val topRoute = _backStack.last()
            popInternal(route = topRoute, result = result)
        }
    }

    fun popToRoot() {
        coroutineScope.launch(Dispatchers.Main) {
            val newTopRoute = _backStack.first()
            setBackStackInternal(listOf(newTopRoute), false, null)
        }
    }

    fun popToRoute(route: Route) {
        coroutineScope.launch(Dispatchers.Main) {
            val index = _backStack.indexOfFirst { it.route == route }
            val newBackStack = _backStack.subList(0, index)
            setBackStackInternal(newBackStack, false, null)
        }
    }

    private suspend fun popInternal(
        route: RouteState,
        result: Any?
    ) = withContext(Dispatchers.Main) {
        d { "pop route ${route.route} with result $result" }
        val newBackStack = _backStack.toMutableList()
        newBackStack -= route
        route.setResult(result)
        setBackStackInternal(newBackStack, false, null)
    }

    fun clear() {
        coroutineScope.launch(Dispatchers.Main) { setBackStack(emptyList(), false, null) }
    }

    fun setBackStack(
        newBackStack: List<Route>,
        isPush: Boolean,
        transition: RouteTransition? = null
    ) {
        coroutineScope.launch(Dispatchers.Main) {
            setBackStackSuspend(newBackStack, isPush, transition)
        }
    }

    suspend fun setBackStackSuspend(
        newBackStack: List<Route>,
        isPush: Boolean,
        transition: RouteTransition? = null
    ) = withContext(Dispatchers.Main) {
        setBackStackInternal(
            newBackStack.map { route ->
                _backStack.firstOrNull { it.route == route } ?: RouteState(route)
            },
            isPush,
            transition
        )
    }

    suspend fun <T> awaitResult(route: Route): T? =
        _backStack.first { it.route == route }.awaitResult()

    private suspend fun setBackStackInternal(
        newBackStack: List<RouteState>,
        isPush: Boolean,
        transition: RouteTransition?
    ) = withContext(Dispatchers.Main) {
        if (newBackStack == _backStack) return@withContext

        d { "set back stack $newBackStack" }

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
            val replacingTopRoutes = newTopRoute != null && (oldTopRoute == null ||
                    oldTopRoute != newTopRoute)

            // Remove all visible routes which shouldn't be visible anymore
            // from top to bottom
            oldVisibleRoutes
                .dropLast(if (replacingTopRoutes) 1 else 0)
                .reversed()
                .filterNot { it in newVisibleRoutes }
                .forEach { route ->
                    val localTransition = transition
                        ?: route.route.exitTransition ?: defaultRouteTransition

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
                    val localTransition =
                        transition ?: route.route.enterTransition ?: defaultRouteTransition
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
                    ?: (if (isPush) newTopRoute?.route?.enterTransition ?: defaultRouteTransition
                    else oldTopRoute?.route?.exitTransition ?: defaultRouteTransition)
                performChange(
                    from = oldTopRoute,
                    to = newTopRoute,
                    isPush = isPush,
                    transition = localTransition
                )
            }
        }

        removedRoutes.forEach {
            it.setResult(null)
            it.dispose()
        }
    }

    private fun performChange(
        from: RouteState?,
        to: RouteState?,
        isPush: Boolean,
        transition: RouteTransition?
    ) {
        val exitFrom = from != null && (isPush && !to!!.route.opaque || !from.route.opaque)
        if (exitFrom) from!!.exit(to = to, isPush = isPush, transition = transition)
        to?.enter(from = from, isPush = isPush, transition = transition)
    }

    internal fun dispose() {
        _backStack.forEach { it.dispose() }
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

        private val retainedObjects = RetainedObjects()
        private val result = CompletableDeferred<Any?>()

        private val overlayEntry = OverlayEntry(
            opaque = route.opaque,
            keepState = route.keepState,
            content = {
                RetainedObjectsAmbient.Provider(retainedObjects) {
                    ProvideCoroutineScope(coroutineScope()) {
                        AbsorbPointer(absorb = transitionRunning) {
                            RouteTransitionWrapper(
                                transition = transition ?: defaultRouteTransition,
                                state = transitionState,
                                lastState = lastTransitionState,
                                onTransitionComplete = onTransitionComplete,
                                types = routeTransitionTypes,
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
            }
        )

        private val onTransitionComplete: (RouteTransition.State) -> Unit = { completedState ->
            if (completedState != RouteTransition.State.Init) {
                transitionRunning = false
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

        private var transitionRunning by framed(false, onSet = { currentValue, newValue ->
            if (currentValue == newValue) return@framed false
            if (newValue) runningTransitions++ else runningTransitions--
            return@framed true
        })

        private fun onOtherTransitionComplete() {
            overlayEntry.opaque = route.opaque
        }

        fun enter(
            from: RouteState?,
            isPush: Boolean,
            transition: RouteTransition?
        ) {
            overlayEntry.opaque = route.opaque || isPush
            transitionRunning = true
            if (isPush) overlayState.add(overlayEntry)
            lastTransitionState = transitionState
            transitionState = if (isPush) RouteTransition.State.EnterFromPush
            else RouteTransition.State.EnterFromPop
            this.transition = transition
        }

        fun exit(
            to: RouteState?,
            isPush: Boolean,
            transition: RouteTransition?
        ) {
            transitionRunning = true
            overlayEntry.opaque = route.opaque || !isPush
            if (isPush) other = to
            lastTransitionState = transitionState
            transitionState = if (isPush) RouteTransition.State.ExitFromPush
            else RouteTransition.State.ExitFromPop
            this.transition = transition
        }

        fun dispose() {
            retainedObjects.dispose()
        }

        suspend fun <T> awaitResult(): T? = result.await() as? T

        fun setResult(result: Any?) {
            if (!this.result.isCompleted) this.result.complete(result)
        }
    }
}

private val NavigatorAmbient = Ambient.of<NavigatorState>()

@Composable
val navigator: NavigatorState
    get() = ambient(NavigatorAmbient)

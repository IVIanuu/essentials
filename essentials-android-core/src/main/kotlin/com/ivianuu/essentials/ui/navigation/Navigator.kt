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
import androidx.compose.Model
import androidx.compose.Observe
import androidx.compose.Providers
import androidx.compose.frames.modelListOf
import androidx.compose.onActive
import androidx.compose.onDispose
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import com.ivianuu.essentials.ui.common.AbsorbPointer
import com.ivianuu.essentials.ui.common.Overlay
import com.ivianuu.essentials.ui.common.OverlayEntry
import com.ivianuu.essentials.ui.common.OverlayState
import com.ivianuu.essentials.ui.common.onBackPressed
import com.ivianuu.essentials.ui.core.RetainedObjects
import com.ivianuu.essentials.ui.core.RetainedObjectsAmbient
import com.ivianuu.essentials.ui.coroutines.CoroutineScopeAmbient
import com.ivianuu.essentials.ui.coroutines.ProvideCoroutineScope
import com.ivianuu.essentials.ui.coroutines.coroutineScope
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Logger
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
) {
    val state = inject<NavigatorState>()

    Observe {
        state.handleBack = handleBack
        state.popsLastRoute = popsLastRoute
        if (!state.hasRoot) {
            state.setRoot(startRoute)
        }
    }

    Navigator(state = state)
}

@Composable
fun Navigator(
    startRoute: Route,
    handleBack: Boolean = true,
    popsLastRoute: Boolean = false
) {
    val coroutineScope = CoroutineScopeAmbient.current
    val dispatchers = inject<AppCoroutineDispatchers>()
    val logger = inject<Logger>()
    val state = remember {
        NavigatorState(
            startRoute = startRoute,
            handleBack = handleBack,
            coroutineScope = coroutineScope,
            dispatchers = dispatchers,
            logger = logger
        )
    }

    state.handleBack = handleBack
    state.popsLastRoute = popsLastRoute

    Navigator(state = state)
}

@Composable
fun Navigator(state: NavigatorState) {
    state.defaultRouteTransition = DefaultRouteTransitionAmbient.current

    Providers(NavigatorAmbient provides state) {
        Overlay(state = state.overlayState)
    }

    val logger = inject<Logger>()

    Observe {
        val enabled = state.handleBack &&
                state.backStack.isNotEmpty() &&
                (state.popsLastRoute || state.backStack.size > 1)
        logger.d("back press enabled $enabled")
        onBackPressed(enabled = enabled) {
            logger.d("on back pressed ${state.runningTransitions}")
            if (state.runningTransitions == 0) state.popTop()
        }
    }

    onDispose { state.dispose() }
}

// todo remove main thread requirement
@Model
class NavigatorState(
    private val coroutineScope: CoroutineScope,
    private val dispatchers: AppCoroutineDispatchers,
    internal val overlayState: OverlayState = OverlayState(),
    startRoute: Route? = null,
    var handleBack: Boolean = true,
    var popsLastRoute: Boolean = false,
    private val logger: Logger
) {

    internal var runningTransitions = 0

    private val _backStack = modelListOf<RouteState>()
    val backStack: List<Route>
        get() = _backStack.map { it.route }

    val hasRoot: Boolean get() = _backStack.isNotEmpty()

    var routeTransitionTypes =
        listOf(ModifierRouteTransitionType)

    internal var defaultRouteTransition = DefaultRouteTransition

    init {
        if (startRoute != null && !hasRoot) {
            setRoot(startRoute)
        }
    }

    fun setRoot(route: Route) {
        setBackStack(listOf(route), true)
    }

    @JvmName("pushWithoutResult")
    fun push(route: Route) {
        @Suppress("DeferredResultUnused")
        coroutineScope.launch(dispatchers.main) { push<Any?>(route) }
    }

    suspend fun <T> push(route: Route): T? = withContext(dispatchers.main) {
        logger.d("push $route")
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack, true, null)
        return@withContext routeState.awaitResult<T>()
            .also { logger.d("on push result for $route -> $it") }
    }

    @JvmName("replaceWithoutResult")
    fun replace(route: Route) {
        @Suppress("DeferredResultUnused")
        coroutineScope.launch(dispatchers.main) { replace<Any?>(route) }
    }

    suspend fun <T> replace(route: Route): T? = withContext(dispatchers.main) {
        logger.d("replace $route")

        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack, true, null)
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
            setBackStackInternal(listOf(newTopRoute), false, null)
        }
    }

    fun popToRoute(route: Route) {
        coroutineScope.launch(dispatchers.main) {
            val index = _backStack.indexOfFirst { it.route == route }
            val newBackStack = _backStack.subList(0, index)
            setBackStackInternal(newBackStack, false, null)
        }
    }

    private suspend fun popInternal(
        route: RouteState,
        result: Any?
    ) = withContext(dispatchers.main) {
        logger.d("pop route ${route.route} with result $result")
        val newBackStack = _backStack.toMutableList()
        newBackStack -= route
        route.setResult(result)
        setBackStackInternal(newBackStack, false, null)
    }

    fun clear() {
        coroutineScope.launch(dispatchers.main) { setBackStack(emptyList(), false, null) }
    }

    fun setBackStack(
        newBackStack: List<Route>,
        isPush: Boolean,
        transition: RouteTransition? = null
    ) {
        coroutineScope.launch(dispatchers.main) {
            setBackStackSuspend(newBackStack, isPush, transition)
        }
    }

    suspend fun setBackStackSuspend(
        newBackStack: List<Route>,
        isPush: Boolean,
        transition: RouteTransition? = null
    ) = withContext(dispatchers.main) {
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
    ) = withContext(dispatchers.main) {
        if (newBackStack == _backStack) return@withContext

        logger.d("set back stack ${newBackStack.map { it.route }}")

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
            val replacingTopRoutes = newTopRoute != null && oldTopRoute != newTopRoute

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
                        isPush = false,
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
        val exitFrom = from != null && (!isPush || !to!!.route.opaque)
        logger.d("perform change from ${from?.route} to ${to?.route} is push $isPush exit from $exitFrom")
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

    @Model
    private inner class RouteState(val route: Route) {

        private val retainedObjects = RetainedObjects()
        private val result = CompletableDeferred<Any?>()

        private val overlayEntry = OverlayEntry(
            opaque = route.opaque,
            keepState = route.keepState,
            content = {
                onActive {
                    logger.d("$name on active")
                    onDispose {
                        logger.d("$name on dispose")
                    }
                }
                Providers(
                    RetainedObjectsAmbient provides retainedObjects,
                    RouteAmbient provides route
                ) {
                    ProvideCoroutineScope(coroutineScope()) {
                        AbsorbPointer(absorb = transitionRunning) {
                            RouteTransitionWrapper(
                                transition = transition ?: defaultRouteTransition,
                                state = transitionState,
                                lastState = lastTransitionState,
                                onTransitionComplete = onTransitionComplete,
                                types = routeTransitionTypes,
                                children = route.content
                            )
                        }
                    }
                }
            }
        )

        private val onTransitionComplete: (RouteTransition.State) -> Unit = { completedState ->
            transitionRunning = false
            runningTransitions--
            lastTransitionState = completedState
            logger.d("$name on transition complete $completedState")
            if (completedState == RouteTransition.State.ExitFromPush) {
                other?.onOtherTransitionComplete()
                other = null
            }

            if ((completedState == RouteTransition.State.ExitFromPush && !route.keepState) ||
                completedState == RouteTransition.State.ExitFromPop) {
                overlayState.remove(overlayEntry)
            }
        }

        private var transition: RouteTransition? = null // todo direct assign
        private var transitionState = RouteTransition.State.Init
        private var lastTransitionState = RouteTransition.State.Init

        private var other: RouteState? = null

        private var transitionRunning = false

        init {
            transition = route.enterTransition // todo direct assign
        }

        private fun onOtherTransitionComplete() {
            overlayEntry.opaque = route.opaque
        }

        private val name: Any get() = route

        fun enter(
            from: RouteState?,
            isPush: Boolean,
            transition: RouteTransition?
        ) {
            overlayEntry.opaque = route.opaque || isPush
            transitionRunning = true
            runningTransitions++

            logger.d("$name enter -> from ${from?.name} is push $isPush trans $transition running trans $runningTransitions")

            val fromIndex = overlayState.entries.indexOf(from?.overlayEntry)
            val toIndex = if (fromIndex != -1) {
                if (isPush) fromIndex + 1 else fromIndex
            } else overlayState.entries.size

            val oldToIndex = overlayState.entries.indexOf(overlayEntry)
            if (oldToIndex == -1) {
                overlayState.add(toIndex, overlayEntry)
            } else if (oldToIndex != toIndex) {
                overlayState.move(oldToIndex, toIndex)
            }

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
            runningTransitions++
            logger.d("$name exit -> to ${to?.name} is push $isPush trans $transition running trans $runningTransitions")

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
            if (!this.result.isCompleted) {
                this.result.complete(result)
            }
        }
    }
}

val NavigatorAmbient =
    staticAmbientOf<NavigatorState>()

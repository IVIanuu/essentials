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
import androidx.compose.onDispose
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import com.ivianuu.essentials.ui.common.Overlay
import com.ivianuu.essentials.ui.common.OverlayEntry
import com.ivianuu.essentials.ui.common.absorbPointer
import com.ivianuu.essentials.ui.common.onBackPressed
import com.ivianuu.essentials.ui.core.RetainedObjects
import com.ivianuu.essentials.ui.core.RetainedObjectsAmbient
import com.ivianuu.essentials.ui.coroutines.compositionCoroutineScope
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
    startRoute: Route,
    handleBack: Boolean = true,
    popsLastRoute: Boolean = false
): Navigator {
    val coroutineScope = compositionCoroutineScope()
    val dispatchers = inject<AppCoroutineDispatchers>()
    val logger = inject<Logger>()
    val navigator = remember {
        Navigator(
            startRoute = startRoute,
            handleBack = handleBack,
            coroutineScope = coroutineScope,
            dispatchers = dispatchers,
            logger = logger
        )
    }

    navigator.handleBack = handleBack
    navigator.popsLastRoute = popsLastRoute

    return navigator
}

// todo remove main thread requirement
// todo main thread requirement comes from compose
@Stable
class Navigator(
    private val coroutineScope: CoroutineScope,
    private val dispatchers: AppCoroutineDispatchers,
    internal val overlay: Overlay = Overlay(),
    startRoute: Route? = null,
    handleBack: Boolean = true,
    popsLastRoute: Boolean = false,
    private val logger: Logger
) {

    var handleBack by mutableStateOf(handleBack)
    var popsLastRoute by mutableStateOf(popsLastRoute)

    internal var runningTransitions by mutableStateOf(0)

    private val _backStack = modelListOf<RouteState>()
    val backStack: List<Route>
        get() = _backStack.map { it.route }

    val hasRoot: Boolean get() = _backStack.isNotEmpty()

    var routeTransitionTypes by mutableStateOf(listOf(ModifierRouteTransitionType))
    internal var defaultRouteTransition by mutableStateOf(DefaultRouteTransition)

    init {
        if (startRoute != null && !hasRoot) {
            setRoot(startRoute)
        }
    }

    @Composable
    fun content() {
        defaultRouteTransition = DefaultRouteTransitionAmbient.current

        val logger = inject<Logger>()

        val enabled = handleBack &&
                backStack.isNotEmpty() &&
                (popsLastRoute || backStack.size > 1)
        logger.d("back press enabled $enabled")
        onBackPressed(enabled = enabled) {
            logger.d("on back pressed ${runningTransitions}")
            if (runningTransitions == 0) popTop()
        }

        onDispose { dispose() }

        Providers(NavigatorAmbient provides this) {
            overlay.content()
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

    private fun dispose() {
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
                Providers(
                    RetainedObjectsAmbient provides retainedObjects,
                    RouteAmbient provides route
                ) {
                    Box(modifier = Modifier.absorbPointer(transitionRunning)) {
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
                completedState == RouteTransition.State.ExitFromPop
            ) {
                overlay.remove(overlayEntry)
            }
        }

        private var transition: RouteTransition? by mutableStateOf(route.enterTransition)
        private var transitionState by mutableStateOf(RouteTransition.State.Init)
        private var lastTransitionState by mutableStateOf(RouteTransition.State.Init)

        private var other: RouteState? by mutableStateOf(null)

        private var transitionRunning by mutableStateOf(false)

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

            val fromIndex = overlay.entries.indexOf(from?.overlayEntry)
            val toIndex = if (fromIndex != -1) {
                if (isPush) fromIndex + 1 else fromIndex
            } else overlay.entries.size

            val oldToIndex = overlay.entries.indexOf(overlayEntry)
            if (oldToIndex == -1) {
                overlay.add(toIndex, overlayEntry)
            } else if (oldToIndex != toIndex) {
                overlay.move(oldToIndex, toIndex)
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
    staticAmbientOf<Navigator>()

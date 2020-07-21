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
import androidx.compose.currentComposer
import androidx.compose.getValue
import androidx.compose.mutableStateListOf
import androidx.compose.mutableStateOf
import androidx.compose.onDispose
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import androidx.ui.savedinstancestate.UiSavedStateRegistry
import androidx.ui.savedinstancestate.UiSavedStateRegistryAmbient
import com.ivianuu.essentials.ui.animatedstack.AnimatedStack
import com.ivianuu.essentials.ui.animatedstack.AnimatedStackChild
import com.ivianuu.essentials.ui.common.onBackPressed
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

@Reader
inline val navigator: Navigator
    get() = given()

@Stable
class Navigator {

    var handleBack by mutableStateOf(true)
    var popsLastRoute by mutableStateOf(false)

    private val _backStack = mutableStateListOf<RouteState>()
    val backStack: List<Route> get() = _backStack.map { it.route }

    val hasRoot: Boolean
        get() = _backStack.isNotEmpty()

    @Composable
    fun content() {
        val backPressEnabled = handleBack &&
                backStack.isNotEmpty() &&
                (popsLastRoute || backStack.size > 1)
        onBackPressed(enabled = backPressEnabled) {
            if (_backStack.none { it.stackChild.isAnimating }) popTop()
        }

        Providers(NavigatorAmbient provides this) {
            AnimatedStack(children = _backStack.map { it.stackChild })
        }
    }

    fun setRoot(content: @Composable () -> Unit) {
        setRoot(Route(content = content))
    }

    fun setRoot(route: Route) {
        setBackStackInternal(listOf(RouteState(route)))
    }

    fun push(content: @Composable () -> Unit) {
        push(Route(content = content))
    }

    fun push(route: Route) {
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack)
    }

    @JvmName("pushForResult")
    suspend fun <T : Any> push(content: @Composable () -> Unit): T? =
        push<T>(Route(content = content))

    @JvmName("pushForResult")
    suspend fun <T : Any> push(route: Route): T? {
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack)
        return (routeState.result as Deferred<T?>).await()
    }

    fun replace(content: @Composable () -> Unit) {
        replace(Route(content = content))
    }

    fun replace(route: Route) {
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
            .also { it.removeAt(it.lastIndex) }
        newBackStack += routeState
        setBackStackInternal(newBackStack)
    }

    @JvmName("replaceForResult")
    suspend fun <T : Any> replace(content: @Composable () -> Unit): T? =
        replace<T>(Route(content = content))

    @JvmName("replaceForResult")
    suspend fun <T : Any> replace(route: Route): T? {
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
            .also { it.removeAt(it.lastIndex) }
        newBackStack += routeState
        setBackStackInternal(newBackStack)
        return (routeState.result as Deferred<T>).await()
    }

    fun popTop(result: Any? = null) {
        popInternal(route = _backStack.last(), result = result)
    }

    fun popToRoot() {
        val newTopRoute = _backStack.first()
        setBackStackInternal(listOf(newTopRoute))
    }

    fun popTo(route: Route) {
        val index = _backStack.indexOfFirst { it.route == route }
        val newBackStack = _backStack.subList(0, index)
        setBackStackInternal(newBackStack)
    }

    fun pop(route: Route, result: Any? = null) {
        popInternal(route = _backStack.first { it.route == route }, result = result)
    }

    private fun popInternal(route: RouteState, result: Any? = null) {
        route.resultToSend = result
        val newBackStack = _backStack.toMutableList()
        newBackStack -= route
        setBackStackInternal(newBackStack)
    }

    fun setBackStack(newBackStack: List<Route>) {
        setBackStackInternal(
            newBackStack.map { route ->
                _backStack.firstOrNull { it.route == route } ?: RouteState(route)
            }
        )
    }

    private fun setBackStackInternal(newBackStack: List<RouteState>) {
        val oldBackStack = _backStack.toList()
        _backStack.clear()
        _backStack += newBackStack
        oldBackStack
            .filterNot { it in newBackStack }
            .forEach { it.detach() }
    }

    private class RouteState(val route: Route) {

        val stackChild = AnimatedStackChild(
            key = this,
            opaque = route.opaque,
            enterTransition = route.enterTransition,
            exitTransition = route.exitTransition
        ) {
            val compositionKey = currentComposer.currentCompoundKeyHash

            val savedStateRegistry = remember {
                UiSavedStateRegistry(
                    restoredValues = savedState.remove(compositionKey),
                    canBeSaved = { true }
                )
            }
            Providers(
                RouteAmbient provides route,
                UiSavedStateRegistryAmbient provides savedStateRegistry
            ) {
                route()
                onDispose {
                    savedState[compositionKey] = savedStateRegistry.performSave()
                }
            }
        }

        private val _result = CompletableDeferred<Any?>()
        val result: Deferred<Any?> get() = _result

        var resultToSend: Any? = null

        private var savedState =
            mutableMapOf<Any, Map<String, Any>>()

        fun detach() {
            _result.complete(resultToSend)
        }

    }

}

val NavigatorAmbient =
    staticAmbientOf<Navigator>()

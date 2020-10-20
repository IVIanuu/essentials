/*
 * Copyright 2020 Manuel Wrage
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

import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.Stable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onActive
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.UiSavedStateRegistry
import androidx.compose.runtime.savedinstancestate.UiSavedStateRegistryAmbient
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticAmbientOf
import com.ivianuu.essentials.ui.UiDecorator
import com.ivianuu.essentials.ui.UiDecoratorBinding
import com.ivianuu.essentials.ui.animatedstack.AnimatedStack
import com.ivianuu.essentials.ui.animatedstack.AnimatedStackChild
import com.ivianuu.essentials.ui.common.OnBackPressed
import com.ivianuu.essentials.ui.common.RetainedObjects
import com.ivianuu.essentials.ui.common.RetainedObjectsAmbient
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Stable
class Navigator {

    var handleBack by mutableStateOf(true)
    var popsLastRoute by mutableStateOf(false)

    private val _backStack = mutableStateListOf<RouteState>()
    val backStack: List<Route> get() = synchronized(this) { _backStack }.map { it.route }

    val hasRoot: Boolean
        get() = synchronized(this) { _backStack }.isNotEmpty()

    @Composable
    fun content() {
        val backStack = synchronized(this) { _backStack }
        val backPressEnabled = handleBack &&
                backStack.isNotEmpty() &&
                (popsLastRoute || backStack.size > 1)
        OnBackPressed(enabled = backPressEnabled) {
            if (backStack.none { it.stackChild.isAnimating }) popTop()
        }

        Providers(NavigatorAmbient provides this) {
            AnimatedStack(children = backStack.map { it.stackChild })
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
        pushInternal(route)
    }

    @JvmName("pushForResult")
    suspend fun <T : Any> push(content: @Composable () -> Unit): T? =
        push<T>(Route(content = content))

    @JvmName("pushForResult")
    suspend fun <T : Any> push(route: Route): T? = pushInternal(route).result.await() as? T

    private fun pushInternal(route: Route): RouteState = synchronized(this) {
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
        newBackStack += routeState
        setBackStackInternal(newBackStack)
        routeState
    }

    fun replace(content: @Composable () -> Unit) {
        replace(Route(content = content))
    }

    fun replace(route: Route) {
        replaceInternal(route)
    }

    @JvmName("replaceForResult")
    suspend fun <T : Any> replace(content: @Composable () -> Unit): T? =
        replace<T>(Route(content = content))

    @JvmName("replaceForResult")
    suspend fun <T : Any> replace(route: Route): T? = replaceInternal(route).result.await() as? T

    private fun replaceInternal(route: Route): RouteState = synchronized(this) {
        val routeState = RouteState(route)
        val newBackStack = _backStack.toMutableList()
            .also { it.removeAt(it.lastIndex) }
        newBackStack += routeState
        setBackStackInternal(newBackStack)
        routeState
    }

    fun popTop(result: Any? = null) = synchronized(this) {
        popInternal(route = _backStack.last(), result = result)
    }

    fun popToRoot() = synchronized(this) {
        val newTopRoute = _backStack.first()
        setBackStackInternal(listOf(newTopRoute))
    }

    fun popTo(route: Route) = synchronized(this) {
        val index = _backStack.indexOfFirst { it.route == route }
        val newBackStack = _backStack.subList(0, index)
        setBackStackInternal(newBackStack)
    }

    fun pop(route: Route, result: Any? = null) = synchronized(this) {
        popInternal(route = _backStack.first { it.route == route }, result = result)
    }

    private fun popInternal(route: RouteState, result: Any? = null) = synchronized(this) {
        route.resultToSend = result
        val newBackStack = _backStack.toMutableList()
        newBackStack -= route
        setBackStackInternal(newBackStack)
    }

    fun setBackStack(newBackStack: List<Route>) = synchronized(this) {
        setBackStackInternal(
            newBackStack.map { route ->
                _backStack.firstOrNull { it.route == route } ?: RouteState(route)
            }
        )
    }

    private fun setBackStackInternal(newBackStack: List<RouteState>) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            GlobalScope.launch(Dispatchers.Main) { setBackStackImpl(newBackStack) }
        } else {
            setBackStackImpl(newBackStack)
        }
    }

    private fun setBackStackImpl(newBackStack: List<RouteState>) {
        synchronized(this) {
            val oldBackStack = _backStack.toList()
            _backStack.clear()
            _backStack += newBackStack
            oldBackStack
                .filterNot { it in newBackStack }
                .forEach { it.detach() }
        }
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
                UiSavedStateRegistryAmbient provides savedStateRegistry,
                RetainedObjectsAmbient provides retainedObjects
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
            mutableMapOf<Any, Map<String, List<Any?>>>()

        private val retainedObjects = RetainedObjects()

        fun detach() {
            _result.complete(resultToSend)
            retainedObjects.dispose()
        }
    }

    companion object {
        @Binding(ApplicationComponent::class)
        fun binding() = Navigator()
    }
}

val NavigatorAmbient =
    staticAmbientOf<Navigator>()

typealias ClearBackStackWhenLeavingApp = UiDecorator

@UiDecoratorBinding
@FunBinding
@Composable
fun ClearBackStackWhenLeavingApp(navigator: Navigator, children: @Composable () -> Unit) {
    onActive {
        onDispose {
            navigator.setBackStack(emptyList())
        }
    }
    children()
}

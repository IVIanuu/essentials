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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.staticAmbientOf
import com.ivianuu.essentials.ui.animatedstack.AnimatedStack
import com.ivianuu.essentials.ui.common.OnBackPressed
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun Navigator.Content(
    handleBack: Boolean = true,
    popsLastRoute: Boolean = false
) {
    val backStack = backStack
    val backPressEnabled = handleBack &&
            backStack.isNotEmpty() &&
            (popsLastRoute || backStack.size > 1)
    OnBackPressed(enabled = backPressEnabled) {
        popTop()
    }

    Providers(NavigatorAmbient provides this) {
        AnimatedStack(children = backStack.map { it.stackChild })
    }
}

@Stable
@Binding(ApplicationComponent::class)
class Navigator {

    private val _backStack = mutableStateListOf<Route>()
    val backStack: List<Route> get() = synchronized(this) { _backStack }

    fun setRoot(content: @Composable () -> Unit) {
        setRoot(Route(content = content))
    }

    fun setRoot(route: Route) {
        setBackStack(listOf(route))
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
    suspend fun <T : Any> push(route: Route): T? {
        pushInternal(route)
        return route.result.await() as? T
    }

    private fun pushInternal(route: Route) = synchronized(this) {
        val newBackStack = _backStack.toMutableList()
        newBackStack += route
        setBackStack(newBackStack)
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
    suspend fun <T : Any> replace(route: Route): T? {
        replaceInternal(route)
        return route.result.await() as? T
    }

    private fun replaceInternal(route: Route) = synchronized(this) {
        val newBackStack = _backStack.toMutableList()
            .also { it.removeAt(it.lastIndex) }
        newBackStack += route
        setBackStack(newBackStack)
    }

    fun popTop(result: Any? = null) = synchronized(this) {
        pop(route = _backStack.last(), result = result)
    }

    fun popToRoot() = synchronized(this) {
        val newTopRoute = _backStack.first()
        setBackStack(listOf(newTopRoute))
    }

    fun popTo(route: Route) = synchronized(this) {
        val index = _backStack.indexOf(route)
        val newBackStack = _backStack.subList(0, index)
        setBackStack(newBackStack)
    }

    fun pop(route: Route, result: Any? = null) = synchronized(this) {
        route.resultToSend = result
        val newBackStack = _backStack.toMutableList()
        newBackStack -= route
        setBackStack(newBackStack)
    }

    fun setBackStack(newBackStack: List<Route>) = synchronized(this) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            GlobalScope.launch(Dispatchers.Main) { setBackStackImpl(newBackStack) }
        } else {
            setBackStackImpl(newBackStack)
        }
    }

    private fun setBackStackImpl(newBackStack: List<Route>) {
        synchronized(this) {
            val oldBackStack = _backStack.toList()
            _backStack.clear()
            _backStack += newBackStack
            oldBackStack
                .filterNot { it in newBackStack }
                .forEach { it.detach() }
        }
    }
}

val NavigatorAmbient = staticAmbientOf<Navigator>()

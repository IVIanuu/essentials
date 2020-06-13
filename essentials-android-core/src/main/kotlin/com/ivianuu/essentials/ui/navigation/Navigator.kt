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
import androidx.compose.FrameManager
import androidx.compose.Providers
import androidx.compose.Stable
import androidx.compose.frames.modelListOf
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import com.ivianuu.essentials.ui.animatedstack.AnimatedStack
import com.ivianuu.essentials.ui.common.onBackPressed
import kotlinx.coroutines.Deferred

@Stable
class Navigator {

    var handleBack by mutableStateOf(true)
    var popsLastRoute by mutableStateOf(false)

    private val _backStack = modelListOf<Route>()
    val backStack: List<Route> get() = _backStack

    val hasRoot: Boolean get() = _backStack.isNotEmpty()

    @Composable
    operator fun invoke() {
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

    fun setRoot(route: Route) {
        setBackStack(listOf(route))
    }

    fun push(route: Route) {
        push<Unit>(route)
    }

    @JvmName("pushForResult")
    fun <T : Any> push(route: Route): Deferred<T?> {
        val newBackStack = _backStack.toMutableList()
        newBackStack += route
        setBackStack(newBackStack)
        return route.result as Deferred<T?>
    }

    fun replace(route: Route) {
        replace<Unit>(route)
    }

    @JvmName("replaceForResult")
    fun <T : Any> replace(route: Route): Deferred<T?> {
        val newBackStack = _backStack.toMutableList()
            .also { it.removeAt(it.lastIndex) }
        newBackStack += route
        setBackStack(newBackStack)
        return route.result as Deferred<T?>
    }

    fun pop(route: Route, result: Any? = null) {
        popInternal(route = route, result = result)
    }

    fun popTop(result: Any? = null) {
        val topRoute = _backStack.last()
        popInternal(route = topRoute, result = result)
    }

    fun popToRoot() {
        val newTopRoute = _backStack.first()
        setBackStack(listOf(newTopRoute))
    }

    fun popTo(route: Route) {
        val index = _backStack.indexOf(route)
        val newBackStack = _backStack.subList(0, index)
        setBackStack(newBackStack)
    }

    private fun popInternal(route: Route, result: Any? = null) {
        val newBackStack = _backStack.toMutableList()
        newBackStack -= route
        setBackStack(newBackStack)
        route.setResult(result)
    }

    fun setBackStack(newBackStack: List<Route>) {
        FrameManager.framed {
            synchronized(this) {
                val oldBackStack = _backStack.toList()
                _backStack.clear()
                _backStack += newBackStack
                oldBackStack
                    .filterNot { it in newBackStack }
                    .forEach { it.dispose() }
            }
        }
    }

}

val NavigatorAmbient =
    staticAmbientOf<Navigator>()

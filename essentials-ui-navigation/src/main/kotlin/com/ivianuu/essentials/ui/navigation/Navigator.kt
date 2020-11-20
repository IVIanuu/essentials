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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticAmbientOf
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.ui.animatedstack.AnimatedStack
import com.ivianuu.essentials.ui.common.OnBackPressed
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.ForEffect
import com.ivianuu.injekt.ImplBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface Navigator {
    val backStack: StateFlow<List<Route>>
    fun setBackStack(transform: suspend (List<Route>) -> List<Route>)
}

fun Navigator.setRootIfEmpty(content: @Composable () -> Unit) {
    setRootIfEmpty(Route(content = content))
}

fun Navigator.setRootIfEmpty(route: Route) {
    setBackStack { if (it.isEmpty()) listOf(route) else it }
}

fun Navigator.setRoot(content: @Composable () -> Unit) {
    setRoot(Route(content = content))
}

fun Navigator.setRoot(route: Route) {
    setBackStack { listOf(route) }
}

fun Navigator.push(content: @Composable () -> Unit) {
    push(Route(content = content))
}

fun Navigator.push(route: Route) {
    setBackStack { it + route }
}

@JvmName("pushForResult")
suspend fun <T : Any> Navigator.push(content: @Composable () -> Unit): T? = push<T>(Route(content = content))

@JvmName("pushForResult")
suspend fun <T : Any> Navigator.push(route: Route): T? {
    setBackStack { it + route }
    return route.awaitResult()
}

fun Navigator.replaceTop(content: @Composable () -> Unit) {
    replaceTop(Route(content = content))
}

fun Navigator.replaceTop(route: Route) {
    setBackStack { it.dropLast(1) + route }
}

fun Navigator.popTop(result: Any? = null) {
    setBackStack {
        val routeToPop = it.last()
        routeToPop.setResult(result)
        it - routeToPop
    }
}

fun Navigator.popToRoot() {
    setBackStack { listOf(it.first()) }
}

fun Navigator.popTo(route: Route) {
    setBackStack {
        val index = it.indexOf(route)
        it.subList(0, index)
    }
}

fun Navigator.pop(route: Route, result: Any? = null) {
    setBackStack {
        route.setResult(result)
        it - route
    }
}

@ImplBinding(ApplicationComponent::class)
class NavigatorImpl(
    scope: GlobalScope,
    initialBackStack: List<Route> = emptyList()
) : Navigator {

    private val _backStack = MutableStateFlow(initialBackStack)
    override val backStack: StateFlow<List<Route>> by this::_backStack

    private val actor = scope.actor<suspend (List<Route>) -> List<Route>>(
        capacity = Channel.UNLIMITED
    ) {
        for (transform in this) {
            val oldBackStack = _backStack.value.toList()
            val newBackStack = transform(oldBackStack)
            if (oldBackStack != newBackStack) {
                _backStack.value = newBackStack
                oldBackStack
                    .filterNot { it in newBackStack }
                    .forEach { it.detach() }
            }
        }
    }

    override fun setBackStack(transform: suspend (List<Route>) -> List<Route>) {
        actor.offer(transform)
    }
}

typealias HomeRoute = Route

@Effect
annotation class HomeRouteUiBinding {
    companion object {
        @Binding
        fun <T : @Composable () -> Unit> homeRoute(instance: @ForEffect T): HomeRoute = Route(
            content = instance as @Composable () -> Unit
        )
    }
}

val NavigatorAmbient = staticAmbientOf<Navigator>()

@Composable
fun Navigator.Content(handleBack: Boolean = true) {
    val currentBackStack by backStack.collectAsState()
    val backPressEnabled = handleBack && currentBackStack.size > 1
    OnBackPressed(enabled = backPressEnabled) {
        popTop()
    }
    Providers(NavigatorAmbient provides this) {
        AnimatedStack(children = currentBackStack.map { it.stackChild })
    }
}

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

import com.github.ajalt.timberkt.d
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.android.ApplicationScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

@ApplicationScope
@Inject
class Navigator {

    private val _backStack = mutableListOf<Route>()
    val backStack: List<Route> get() = synchronized(_backStack) { _backStack }

    val flow: Flow<List<Route>>
        get() = channel.openSubscription().consumeAsFlow()

    private val channel = ConflatedBroadcastChannel(emptyList<Route>())

    private val resultsByRoute = mutableMapOf<Route, CompletableDeferred<Any?>>()

    @JvmName("pushWithoutResult")
    fun push(route: Route) {
        @Suppress("DeferredResultUnused")
        GlobalScope.launch { push<Any?>(route) }
    }

    suspend fun <T> push(route: Route): T? {
        d { "push $route" }
        val newBackStack = backStack.toMutableList()
        newBackStack += route
        setBackStack(newBackStack)
        val deferredResult = CompletableDeferred<Any?>()
        synchronized(resultsByRoute) { resultsByRoute[route] = deferredResult }
        return deferredResult.await() as? T
    }

    fun pop(result: Any? = null) {
        d { "pop result $result" }
        val newBackStack = backStack.toMutableList()
        val removedRoute = newBackStack.removeAt(backStack.lastIndex)
        setBackStack(newBackStack)
        val deferredResult = synchronized(resultsByRoute) { resultsByRoute.remove(removedRoute) }
        deferredResult?.complete(result)
    }

    private fun setBackStack(newBackStack: List<Route>) {
        synchronized(_backStack) {
            _backStack.clear()
            _backStack += newBackStack
        }
        channel.offer(newBackStack)
    }

}
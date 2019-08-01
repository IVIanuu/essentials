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

import hu.akarnokd.kotlin.flow.BehaviorSubject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Navigator {

    private val _backStack = mutableListOf<Route>()
    val backStack: List<Route> get() = synchronized(this) { _backStack }

    val flow: Flow<List<Route>>
        get() = subject

    private val subject = BehaviorSubject(emptyList<Route>())

    private val resultsByRoute = mutableMapOf<Route, CompletableDeferred<Any?>>()

    @JvmName("pushWithoutResult")
    fun push(route: Route) {
        push<Any?>(route)
    }

    fun <T> push(route: Route): Deferred<T?> = synchronized(this) {
        val newBackStack = backStack.toMutableList()
        newBackStack.add(route)
        setBackStack(newBackStack)
        val result = CompletableDeferred<Any?>()
        resultsByRoute[route] = result
        return@synchronized result as Deferred<T?>
    }

    fun pop(result: Any? = null) = synchronized(this) {
        val newBackStack = backStack.toMutableList()
        val removedRoute = newBackStack.removeAt(backStack.lastIndex)
        setBackStack(newBackStack)
        resultsByRoute.remove(removedRoute)?.complete(result)
    }

    private fun setBackStack(newBackStack: List<Route>) {
        _backStack.clear()
        _backStack.addAll(newBackStack)
        GlobalScope.launch { subject.emit(_backStack) } // todo this is ugly
    }

}
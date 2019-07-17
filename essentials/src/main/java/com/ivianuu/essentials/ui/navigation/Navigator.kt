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

import com.ivianuu.essentials.util.BehaviorSubject
import io.reactivex.Observable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

class Navigator {

    private val _backStack = mutableListOf<Route>()
    val backStack: List<Route> get() = _backStack

    val observable: Observable<List<Route>>
        get() = subject

    private val subject = BehaviorSubject(backStack)

    private val resultsByRoute = mutableMapOf<Route, CompletableDeferred<Any?>>()

    fun push(route: Route): Deferred<Any?> {
        val newBackStack = backStack.toMutableList()
        newBackStack.add(route)
        setBackStack(newBackStack)
        val result = CompletableDeferred<Any?>()
        resultsByRoute[route] = result
        return result
    }

    fun pop(result: Any? = null) {
        val newBackStack = backStack.toMutableList()
        val removedRoute = newBackStack.removeAt(backStack.lastIndex)
        setBackStack(newBackStack)
        resultsByRoute.remove(removedRoute)?.complete(result)
    }

    fun replaceTop(route: Route) {
        val newBackStack = backStack.toMutableList()
        if (newBackStack.isNotEmpty()) newBackStack.removeAt(newBackStack.lastIndex)
        newBackStack.add(route)
        setBackStack(newBackStack)
    }

    private fun setBackStack(newBackStack: List<Route>) {
        _backStack.clear()
        _backStack.addAll(newBackStack)
        subject.onNext(_backStack)
    }

}
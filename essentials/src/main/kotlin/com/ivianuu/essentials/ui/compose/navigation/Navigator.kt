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

package com.ivianuu.essentials.ui.compose.navigation

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.frames.ModelList
import androidx.compose.frames.modelListOf
import androidx.compose.memo
import androidx.compose.unaryPlus
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.Overlay
import com.ivianuu.essentials.ui.compose.common.onBackPressed
import com.ivianuu.essentials.ui.compose.common.retained
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.coroutines.coroutineScope
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.util.AppDispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// todo transition support

@Composable
fun Navigator(
    handleBack: Boolean = true,
    key: String? = null,
    startRoute: () -> Route
) = composable("Navigator") {
    val overlay = +memo { Overlay() }
    val coroutineScope = +coroutineScope()
    val appDispatchers = +inject<AppDispatchers>()
    val navigator = +retained("Navigator:${key.orEmpty()}") {
        Navigator(overlay, coroutineScope, appDispatchers, modelListOf(), startRoute())
    }

    if (handleBack && navigator.backStack.size > 1) {
        composable("onBackPressed") {
            +onBackPressed { navigator.pop() }
        }
    }

    navigator.compose()
}

class Navigator internal constructor(
    val overlay: Overlay,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: AppDispatchers,
    private val _backStack: ModelList<Route>,
    startRoute: Route
) {

    val backStack: List<Route>
        get() = _backStack

    private val resultsByRoute = mutableMapOf<Route, CompletableDeferred<Any?>>()

    init {
        if (_backStack.isEmpty()) {
            push(startRoute)
        }
    }

    @JvmName("pushWithoutResult")
    fun push(route: Route) {
        @Suppress("DeferredResultUnused")
        coroutineScope.launch { push<Any?>(route) }
    }

    suspend fun <T> push(route: Route): T? {
        d { "push $route" }
        _backStack += route
        route.onPush(this, _backStack.lastIndex)
        val deferredResult = CompletableDeferred<Any?>()
        synchronized(resultsByRoute) { resultsByRoute[route] = deferredResult }
        return deferredResult.await() as? T
    }

    fun pop(result: Any? = null) {
        coroutineScope.launch { popInternal(result) }
    }

    private suspend fun popInternal(result: Any?) {
        d { "pop result $result" }
        val removedRoute = _backStack.removeAt(backStack.lastIndex)
        removedRoute.onPop()
        val deferredResult = synchronized(resultsByRoute) { resultsByRoute.remove(removedRoute) }
        deferredResult?.complete(result)
    }

    @JvmName("replaceWithoutResult")
    fun replace(route: Route) {
        @Suppress("DeferredResultUnused")
        coroutineScope.launch { replace<Any?>(route) }
    }

    suspend fun <T> replace(route: Route): T? {
        d { "replace $route" }

        if (_backStack.isNotEmpty()) {
            val removedRoute = _backStack.removeAt(_backStack.lastIndex)
            removedRoute.onPop()
        }

        _backStack += route
        route.onPush(this, _backStack.lastIndex)

        val deferredResult = CompletableDeferred<Any?>()
        synchronized(resultsByRoute) { resultsByRoute[route] = deferredResult }

        return deferredResult.await() as? T
    }

    @Composable
    internal fun compose() {
        NavigatorAmbient.Provider(this) {
            overlay.compose()
        }
    }

}

val NavigatorAmbient = Ambient.of<Navigator>()
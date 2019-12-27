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

package com.ivianuu.essentials.mvrx

import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.coroutines.StateFlow
import com.ivianuu.essentials.coroutines.setIfChanged
import com.ivianuu.essentials.ui.base.ViewModel
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Fail
import com.ivianuu.essentials.util.Loading
import com.ivianuu.essentials.util.Success
import com.ivianuu.essentials.util.asFail
import com.ivianuu.essentials.util.asSuccess
import com.ivianuu.essentials.util.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * State view model
 */
abstract class MvRxViewModel<S>(initialState: S) : ViewModel() {

    private val _state = StateFlow(initialState)
    val flow: Flow<S> get() = _state
    val state: S get() = _state.value

    protected suspend fun setState(reducer: suspend S.() -> S) {
        // todo we need a way to use the AppDispatchers.default dispatcher
        withContext(Dispatchers.Default) {
            val currentState = _state.value
            val newState = reducer(currentState)
            _state.setIfChanged(newState)
        }
    }

    fun logStateChanges() {
        subscribe { d { "new state -> $it" } }
    }

    protected fun subscribe(consumer: suspend (S) -> Unit): Job =
        flow.onEach(consumer)
            .flowOn(Dispatchers.Default)
            .launchIn(scope.coroutineScope)

    protected fun <V> Deferred<V>.execute(
        context: CoroutineContext = Dispatchers.Default,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        reducer: S.(Async<V>) -> S
    ): Job = scope.coroutineScope.execute(
        context = context,
        start = start,
        block = { await() },
        reducer = reducer
    )

    protected suspend fun <V> Flow<V>.execute(reducer: S.(Async<V>) -> S) {
        setState { reducer(Loading()) }
        return this
            .map { it.asSuccess() }
            .catch { it.asFail<V>() }
            .flowOn(Dispatchers.Default)
            .collect { setState { reducer(it) } }
    }

    protected fun <V> Flow<V>.executeIn(scope: CoroutineScope, reducer: S.(Async<V>) -> S): Job {
        return scope.launch(Dispatchers.Default) {
            setState { reducer(Loading()) }
            this@executeIn
                .map { it.asSuccess() }
                .catch { it.asFail<V>() }
                .collect { setState { reducer(it) } }
        }
    }

    protected fun <V> CoroutineScope.execute(
        context: CoroutineContext = Dispatchers.Default,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend () -> V,
        reducer: S.(Async<V>) -> S
    ): Job = launch(context, start) {
        setState { reducer(Loading()) }
        try {
            val result = block()
            setState { reducer(Success(result)) }
        } catch (e: Exception) {
            setState { reducer(Fail(e)) }
        }
    }

    override fun toString() = "${javaClass.simpleName} -> $state"
}

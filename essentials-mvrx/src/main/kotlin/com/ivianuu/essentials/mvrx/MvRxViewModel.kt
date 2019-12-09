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

import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.base.EsViewModel
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Fail
import com.ivianuu.essentials.util.Loading
import com.ivianuu.essentials.util.Success
import com.ivianuu.essentials.util.asFail
import com.ivianuu.essentials.util.asSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * State view model
 */
abstract class MvRxViewModel<S>(initialState: S) : EsViewModel() {

    private val channel = ConflatedBroadcastChannel(initialState)
    val flow: Flow<S>
        get() = channel.asFlow()

    private var _state: S = initialState
    val state: S get() = synchronized(stateLock) { _state }
    private val stateLock = Any()

    protected suspend fun setState(reducer: suspend S.() -> S) {
        // todo we need a way to use the AppDispatchers.default dispatcher
        withContext(Dispatchers.Default) {
            val currentState = synchronized(stateLock) { _state }
            val newState = reducer(currentState)
            if (currentState != newState) {
                synchronized(stateLock) { _state = newState }
                channel.offer(newState)
            }
        }
    }

    fun logStateChanges() {
        subscribe { d { "new state -> $it" } }
    }

    protected fun subscribe(consumer: suspend (S) -> Unit): Job =
        flow.onEach(consumer).launchIn(viewModelScope)

    protected fun <V> Deferred<V>.execute(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        reducer: S.(Async<V>) -> S
    ): Job = viewModelScope.execute(
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
            .collect { setState { reducer(it) } }
    }

    protected fun <V> Flow<V>.executeIn(scope: CoroutineScope, reducer: S.(Async<V>) -> S): Job {
        return scope.launch {
            setState { reducer(Loading()) }
            this@executeIn
                .map { it.asSuccess() }
                .catch { it.asFail<V>() }
                .collect { setState { reducer(it) } }
        }
    }

    protected fun <V> CoroutineScope.execute(
        context: CoroutineContext = EmptyCoroutineContext,
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

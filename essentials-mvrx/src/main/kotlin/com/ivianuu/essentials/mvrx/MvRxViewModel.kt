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

import com.ivianuu.essentials.android.ui.base.ViewModel
import com.ivianuu.essentials.android.util.Async
import com.ivianuu.essentials.android.util.Fail
import com.ivianuu.essentials.android.util.Loading
import com.ivianuu.essentials.android.util.Success
import com.ivianuu.essentials.app.AppComponentHolder
import com.ivianuu.essentials.coroutines.StateFlow
import com.ivianuu.essentials.coroutines.setIfChanged
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
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
    val state: Flow<S> get() = _state

    fun getCurrentState(): S = _state.value

    protected open val coroutineDispatcher = AppComponentHolder
        .get<AppCoroutineDispatchers>().computation

    init {
        subscribe { AppComponentHolder.get<Logger>().d("new state -> $it") }
    }

    protected suspend fun setState(reducer: suspend S.() -> S) {
        withContext(coroutineDispatcher) {
            val currentState = _state.value
            val newState = reducer(currentState)
            _state.setIfChanged(newState)
        }
    }

    protected fun subscribe(consumer: suspend (S) -> Unit): Job =
        state.onEach(consumer)
            .flowOn(coroutineDispatcher)
            .launchIn(coroutineScope)

    protected fun <V> Deferred<V>.execute(
        context: CoroutineContext = coroutineDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        reducer: S.(Async<V>) -> S
    ): Job = coroutineScope.execute(
        context = context,
        start = start,
        block = { await() },
        reducer = reducer
    )

    protected suspend fun <V> Flow<V>.execute(reducer: S.(Async<V>) -> S) {
        setState { reducer(Loading()) }
        return this
            .map { Success(it) }
            .catch { Fail<V>(it) }
            .flowOn(coroutineDispatcher)
            .collect { setState { reducer(it) } }
    }

    protected fun <V> Flow<V>.executeIn(scope: CoroutineScope, reducer: S.(Async<V>) -> S): Job {
        return scope.launch(coroutineDispatcher) {
            setState { reducer(Loading()) }
            this@executeIn
                .map { Success(it) }
                .catch { Fail<V>(it) }
                .collect { setState { reducer(it) } }
        }
    }

    protected fun <V> CoroutineScope.execute(
        context: CoroutineContext = coroutineDispatcher,
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

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

package com.ivianuu.essentials.ui.viewmodel

import androidx.compose.Composable
import androidx.compose.collectAsState
import com.ivianuu.essentials.ui.resource.Error
import com.ivianuu.essentials.ui.resource.Loading
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.Success
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * State view model
 */
@Reader
abstract class StateViewModel<S>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> get() = _state

    private val actor = scope.actor<suspend S.() -> S> {
        for (reducer in this) {
            val currentState = _state.value
            val newState = reducer(currentState)
            _state.value = newState
        }
    }

    protected fun setState(reducer: suspend S.() -> S) {
        actor.offer(reducer)
    }

    protected suspend fun setStateNow(reducer: suspend S.() -> S) {
        actor.send(reducer)
    }

    protected fun <V> Flow<V>.execute(reducer: suspend S.(Resource<V>) -> S): Job {
        return scope.launch {
            setStateNow { reducer(Loading) }
            this@execute
                .map { Success(it) }
                .catch { Error(it) }
                .collect { setStateNow { reducer(it) } }
        }
    }

    protected suspend fun <V> Flow<V>.executeNow(reducer: suspend S.(Resource<V>) -> S) {
        setStateNow { reducer(Loading) }
        return this
            .map { Success(it) }
            .catch { Error(it) }
            .collect { setStateNow { reducer(it) } }
    }

    protected fun <V> execute(
        context: CoroutineContext = scope.coroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> V,
        reducer: suspend S.(Resource<V>) -> S
    ): Job = scope.launch(context, start) {
        setStateNow { reducer(Loading) }
        try {
            val result = block()
            setStateNow { reducer(Success(result)) }
        } catch (e: Exception) {
            setStateNow { reducer(Error(e)) }
        }
    }

    override fun toString() = "${javaClass.simpleName} -> ${state.value}"
}

@Composable
val <S> StateViewModel<S>.currentState: S
    get() = state.collectAsState().value

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
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.flowAsResource
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * State view model
 */
@Reader
abstract class StateViewModel<S>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> get() = _state

    private val actor = scope.actor<SetState> {
        for (msg in this) {
            val currentState = _state.value
            val newState = msg.reducer(currentState)
            _state.value = newState
            msg.acknowledged?.complete(newState)
        }
    }

    protected fun setState(reducer: suspend S.() -> S) {
        actor.offer(SetState(reducer, null))
    }

    protected suspend fun setStateNow(reducer: suspend S.() -> S): S {
        val acknowledged = CompletableDeferred<S>()
        actor.offer(SetState(reducer, acknowledged))
        return acknowledged.await()
    }

    private inner class SetState(
        val reducer: suspend S.() -> S,
        val acknowledged: CompletableDeferred<S>?
    )

    protected fun <V> Flow<V>.execute(reducer: suspend S.(Resource<V>) -> S): Job {
        return flowAsResource()
            .onEach { setStateNow { reducer(it) } }
            .launchIn(scope)
    }

    protected fun <V> execute(
        block: suspend () -> V,
        reducer: suspend S.(Resource<V>) -> S
    ): Job {
        return flow { emit(block()) }
            .execute(reducer)
    }

    override fun toString() = "${javaClass.simpleName} -> ${state.value}"
}

@Composable
val <S> StateViewModel<S>.currentState: S
    get() = state.collectAsState().value

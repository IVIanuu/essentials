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

package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import java.util.*

internal class StateStore<S>(
    initialState: S,
    val scope: CoroutineScope
) {

    private val _liveData = MutableLiveData<S>()
    val state: LiveData<S> get() = _liveData

    private var _state: S = initialState
    val currentState: S
        get() = synchronized(this) { _state }

    private val actionsActor = scope.actor<Action<S>>(
        context = Dispatchers.Default,
        capacity = Channel.UNLIMITED
    ) {

        val getStateQueue = ArrayDeque<(S) -> Unit>()

        consumeEach { action ->
            when (action) {
                is SetStateAction -> {
                    val currentState = synchronized(this@StateStore) { _state }
                    val newState = action.reducer(currentState)
                    synchronized(this@StateStore) { _state = newState }
                    _liveData.postValue(newState)
                }
                is GetStateAction -> {
                    getStateQueue.offer(action.block)
                }
            }

            val snapshot = synchronized(this@StateStore) { _state }

            getStateQueue
                .takeWhile { channel.isEmpty }
                .map { block ->
                    block(snapshot)
                    getStateQueue.removeFirst()
                }
        }
    }

    fun set(reducer: S.() -> S) {
        actionsActor.offer(SetStateAction(reducer))
    }

    fun get(block: (S) -> Unit) {
        actionsActor.offer(GetStateAction(block))
    }

}

internal interface Action<S>
internal inline class SetStateAction<S>(val reducer: S.() -> S) : Action<S>
internal inline class GetStateAction<S>(val block: (S) -> Unit) : Action<S>
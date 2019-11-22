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

package com.ivianuu.essentials.ui.compose.mvi

import androidx.compose.Composable
import androidx.compose.sourceLocation
import com.ivianuu.essentials.ui.compose.common.retainedState
interface StateStore<S, A> {
    val state: S
    fun dispatch(action: A)

    operator fun component1(): S = state
    operator fun component2(): (A) -> Unit = this::dispatch
}

@Composable
inline fun <S, A> stateStore(
    noinline initialState: () -> S,
    noinline reducer: (currentState: S, action: A) -> S
): StateStore<S, A> = stateStore(
    key = Integer.valueOf(sourceLocation()),
    initialState = initialState,
    reducer = reducer
)

@Composable
fun <S, A> stateStore(
    key: Any = "StateStore",
    initialState: () -> S,
    reducer: (currentState: S, action: A) -> S
): StateStore<S, A> {
    val stateHolder = retainedState(key, false, initialState)
    return object : StateStore<S, A> {
        override val state: S
            get() = stateHolder.value

        override fun dispatch(action: A) {
            stateHolder.value = reducer(stateHolder.value, action)
        }
    }
}
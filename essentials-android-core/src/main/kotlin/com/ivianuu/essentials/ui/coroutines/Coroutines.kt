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

package com.ivianuu.essentials.ui.coroutines

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.State
import androidx.compose.launchInComposition
import androidx.compose.onDispose
import androidx.compose.remember
import androidx.compose.state
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.CoroutineContext

@Composable
fun compositionCoroutineScope(context: CoroutineContext = Dispatchers.Main): CoroutineScope {
    val coroutineScope = remember(context) { CoroutineScope(context + Job()) }
    onDispose { coroutineScope.coroutineContext[Job]!!.cancel() }
    return coroutineScope
}

@BuilderInference
@Composable
fun <T> launchForResult(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> T
): State<T?> {
    return launchForResult(
        initial = null,
        inputs = *inputs,
        block = block
    )
}

@Composable
fun <T> launchForResult(
    vararg inputs: Any?,
    initial: T,
    block: suspend CoroutineScope.() -> T
): State<T> {
    val state = state { initial }
    launchWithState(
        inputs = *inputs,
        initial = initial
    ) {
        state.value = block()
    }
    return state
}

@BuilderInference
@Composable
fun <T> launchWithState(
    vararg inputs: Any?,
    block: suspend StateCoroutineScope<T?>.() -> Unit
): State<T?> = launchWithState(
    initial = null,
    inputs = *inputs,
    block = block
)

@Composable
fun <T> launchWithState(
    vararg inputs: Any?,
    initial: T,
    block: suspend StateCoroutineScope<T>.() -> Unit
): State<T> {
    val state = state { initial }
    launchInComposition(*inputs) {
        with(StateCoroutineScope(this, state)) {
            block()
        }
    }
    return state
}

class StateCoroutineScope<T>(
    private val coroutineScope: CoroutineScope,
    val state: MutableState<T>
) : CoroutineScope by coroutineScope

@Composable
fun <T> StateFlow<T>.collectAsState(): State<T> = collectAsState(value)

@Composable
fun <T> Flow<T>.collectAsState(): State<T?> = collectAsState(null)

@Composable
fun <T> Flow<T>.collectAsState(initial: T): State<T> {
    return launchWithState(
        initial = initial,
        inputs = *arrayOf(this),
        block = {
            collect {
                state.value = it
            }
        }
    )
}

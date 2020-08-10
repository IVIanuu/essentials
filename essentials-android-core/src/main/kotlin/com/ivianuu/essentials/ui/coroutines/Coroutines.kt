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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.state
import com.ivianuu.essentials.ui.common.rememberRetained
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T> produceState(
    initial: T,
    block: suspend CoroutineScope.() -> T
): State<T> = produceState(initial = initial, inputs = *emptyArray(), block = block)

@Composable
fun <T> produceState(
    initial: T,
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> T
): State<T> {
    val state = state { initial }
    launchInComposition(*inputs) { state.value = block() }
    return state
}

@Composable
fun rememberRetainedCoroutinesScope(
    getContext: () -> CoroutineContext = { EmptyCoroutineContext }
): CoroutineScope = rememberRetained { ClosableCoroutineScope(CoroutineScope(getContext())) }

private class ClosableCoroutineScope(
    private val coroutineScope: CoroutineScope
) : CoroutineScope by coroutineScope, DisposableHandle {
    override fun dispose() {
        coroutineScope.cancel()
    }
}

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

package com.ivianuu.essentials.ui.compose.coroutines

import androidx.compose.Composable
import androidx.ui.core.CoroutineContextAmbient
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.effect
import com.ivianuu.essentials.ui.compose.core.memo
import com.ivianuu.essentials.ui.compose.core.onActive
import com.ivianuu.essentials.ui.compose.core.onCommit
import com.ivianuu.essentials.ui.compose.core.onDispose
import com.ivianuu.essentials.ui.compose.core.onPreCommit
import com.ivianuu.essentials.ui.compose.core.state
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@Composable
fun coroutineScope(context: @Composable() () -> CoroutineContext = { coroutineContext() }): CoroutineScope =
    effect {
        val coroutineContext = context()
        val coroutineScope = memo { CoroutineScope(context = coroutineContext + Job()) }
        onDispose { coroutineScope.coroutineContext[Job]!!.cancel() }
        return@effect coroutineScope
    }

@Composable
fun coroutineContext() = effect {
    ambient(CoroutineContextAmbient)
}

@Composable
fun launchOnActive(
    block: suspend CoroutineScope.() -> Unit
) = effect {
    val coroutineScope = coroutineScope()
    onActive {
        coroutineScope.launch(block = block)
    }
}

@Composable
fun launchOnPreCommit(
    block: suspend CoroutineScope.() -> Unit
) = effect {
    val coroutineScope = coroutineScope()
    onPreCommit { coroutineScope.launch(block = block) }
}

@Composable
fun launchOnPreCommit(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> Unit
) = effect {
    val coroutineScope = coroutineScope()
    onPreCommit(*inputs) { coroutineScope.launch(block = block) }
}

@Composable
fun launchOnCommit(
    block: suspend CoroutineScope.() -> Unit
) = effect {
    val coroutineScope = coroutineScope()
    onCommit { coroutineScope.launch(block = block) }
}

@Composable
fun launchOnCommit(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> Unit
) = effect {
    val coroutineScope = coroutineScope()
    onCommit(*inputs) { coroutineScope.launch(block = block) }
}

@BuilderInference
@Composable
fun <T> load(block: suspend CoroutineScope.() -> T) = effect {
    load(
        placeholder = null,
        block = block
    )
}

@Composable
fun <T> load(
    placeholder: T,
    block: suspend CoroutineScope.() -> T
): T = effect {
    val state = state { placeholder }
    launchOnActive { state.value = block() }
    return@effect state.value
}

@Composable
fun <T> collect(flow: Flow<T>) = effect {
    collect(null, flow)
}

@Composable
fun <T> collect(
    placeholder: T,
    flow: Flow<T>
): T = effect {
    val state = state { placeholder }
    val coroutineScope = coroutineScope()
    onActive {
        flow
            .onEach { state.value = it }
            .launchIn(coroutineScope)
    }
    return@effect state.value
}
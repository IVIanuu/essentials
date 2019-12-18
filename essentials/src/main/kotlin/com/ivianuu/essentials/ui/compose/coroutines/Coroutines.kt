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
import androidx.compose.ambient
import androidx.compose.onActive
import androidx.compose.onCommit
import androidx.compose.onDispose
import androidx.compose.onPreCommit
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.CoroutineContextAmbient
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.onFinalDispose
import com.ivianuu.essentials.ui.compose.common.retained
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

// todo retainedLaunchOnActive
// todo retainedLoad

@Composable
fun retainedCoroutineScope(
    key: Any,
    context: @Composable() () -> CoroutineContext = {
        retainedCoroutineContext(key = key)
    }
): CoroutineScope {
    val coroutineContext = context()
    val coroutineScope = remember { CoroutineScope(context = coroutineContext) }
    return coroutineScope
}

@Composable
fun retainedCoroutineContext(key: Any): CoroutineContext {
    val coroutineContext = retained(key = key) { Job() + Dispatchers.Main }
    onFinalDispose {
        d { "on final dispose" }
        coroutineContext[Job]!!.cancel()
    }
    return coroutineContext
}

@Composable
fun coroutineScope(context: @Composable() () -> CoroutineContext = { coroutineContext() }): CoroutineScope {
    val coroutineContext = context()
    val coroutineScope = remember { CoroutineScope(context = coroutineContext) }
    return coroutineScope
}

@Composable
fun coroutineContext(): CoroutineContext {
    val parent = ambient(CoroutineContextAmbient)
    val coroutineContext = remember { Job(parent = parent[Job]) + Dispatchers.Main }
    onDispose { coroutineContext[Job]!!.cancel() }
    return coroutineContext
}

@Composable
fun launchOnActive(
    block: suspend CoroutineScope.() -> Unit
) {
    val coroutineScope = coroutineScope()
    onActive {
        val job = coroutineScope.launch(block = block)
        onDispose { job.cancel() }
    }
}

@Composable
fun launchOnPreCommit(
    block: suspend CoroutineScope.() -> Unit
) {
    val coroutineScope = coroutineScope()
    onPreCommit {
        val job = coroutineScope.launch(block = block)
        onDispose { job.cancel() }
    }
}

@Composable
fun launchOnPreCommit(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    val coroutineScope = coroutineScope()
    onPreCommit(*inputs) {
        val job = coroutineScope.launch(block = block)
        onDispose { job.cancel() }
    }
}

@Composable
fun launchOnCommit(
    block: suspend CoroutineScope.() -> Unit
) {
    val coroutineScope = coroutineScope()
    onCommit {
        val job = coroutineScope.launch(block = block)
        onDispose { job.cancel() }
    }
}

@Composable
fun launchOnCommit(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    val coroutineScope = coroutineScope()
    onCommit(*inputs) {
        val job = coroutineScope.launch(block = block)
        onDispose { job.cancel() }
    }
}

@BuilderInference
@Composable
fun <T> load(
    key: Any,
    block: suspend CoroutineScope.() -> T
): T? = load(
    placeholder = null,
    key = key,
    block = block
)

@Composable
fun <T> load(
    placeholder: T,
    key: Any,
    block: suspend CoroutineScope.() -> T
): T {
    val state = state { placeholder }
    launchOnCommit(key) { state.value = block() }
    return state.value
}

@Composable
fun <T> collect(flow: Flow<T>): T? = collect(flow, null)

@Composable
fun <T> collect(
    flow: Flow<T>,
    placeholder: T
): T {
    val state = state { placeholder }
    launchOnCommit(flow) {
        flow.collect {
            state.value = it
        }
    }
    return state.value
}

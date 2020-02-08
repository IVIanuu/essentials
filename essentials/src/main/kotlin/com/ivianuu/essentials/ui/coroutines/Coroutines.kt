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
import androidx.compose.Providers
import androidx.compose.onActive
import androidx.compose.onCommit
import androidx.compose.onDispose
import androidx.compose.onPreCommit
import androidx.compose.remember
import androidx.compose.state
import androidx.compose.staticAmbientOf
import androidx.ui.core.CoroutineContextAmbient
import com.ivianuu.essentials.app.AppComponent
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Fail
import com.ivianuu.essentials.util.Loading
import com.ivianuu.essentials.util.Success
import com.ivianuu.essentials.util.Uninitialized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

@Composable
val CoroutineScopeAmbient =
    staticAmbientOf<CoroutineScope> { error("No coroutineScope provided") }

@Composable
fun ProvideCoroutineScope(
    coroutineScope: CoroutineScope,
    children: @Composable () -> Unit
) {
    Providers(
        CoroutineContextAmbient provides coroutineScope.coroutineContext,
        CoroutineScopeAmbient provides coroutineScope,
        children = children
    )
}

@Composable
fun coroutineScope(context: CoroutineContext = defaultCoroutineContext()): CoroutineScope {
    val coroutineScope = remember { CoroutineScope(context) }
    onDispose { context[Job]!!.cancel() }
    return coroutineScope
}

@Composable
private fun defaultCoroutineContext(): CoroutineContext =
    remember { Job() + AppComponent.get().get<AppCoroutineDispatchers>().main }

@Composable
fun launchOnActive(
    block: suspend CoroutineScope.() -> Unit
) {
    val coroutineScope = CoroutineScopeAmbient.current
    onActive {
        val job = coroutineScope.launch(block = block)
        onDispose { job.cancel() }
    }
}

@Composable
fun launchOnPreCommit(
    block: suspend CoroutineScope.() -> Unit
) {
    val coroutineScope = CoroutineScopeAmbient.current
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
    val coroutineScope = CoroutineScopeAmbient.current
    onPreCommit(*inputs) {
        val job = coroutineScope.launch(block = block)
        onDispose { job.cancel() }
    }
}

@Composable
fun launchOnCommit(
    block: suspend CoroutineScope.() -> Unit
) {
    val coroutineScope = CoroutineScopeAmbient.current
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
    val coroutineScope = CoroutineScopeAmbient.current
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
    val dispatchers = inject<AppCoroutineDispatchers>()
    launchOnCommit(key) {
        val result = block()
        withContext(dispatchers.main) {
            // todo remove once safe
            state.value = result
        }
    }
    return state.value
}

@Composable
fun <T> loadAsync(
    key: Any,
    block: suspend CoroutineScope.() -> T
): Async<T> {
    val state = state<Async<T>> { Uninitialized }
    val dispatchers = inject<AppCoroutineDispatchers>()
    launchOnCommit(key) {
        state.value = withContext(dispatchers.main) {
            // todo remove once safe
            Loading()
        }
        try {
            val result = block()
            withContext(dispatchers.main) {
                // todo remove once safe
                state.value = Success(result)
            }
        } catch (e: Exception) {
            withContext(dispatchers.main) {
                // todo remove once safe
                state.value = Fail(e)
            }
        }
    }
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
    val dispatchers = inject<AppCoroutineDispatchers>()
    launchOnCommit(flow) {
        flow
            .collect { item ->
                withContext(dispatchers.main) {
                    // todo remove once safe
                    state.value = item
                }
            }
    }
    return state.value
}

@Composable
fun <T> collectAsync(flow: Flow<T>): Async<T> {
    val state = state<Async<T>> { Uninitialized }
    val dispatchers = inject<AppCoroutineDispatchers>()
    launchOnCommit(flow) {
        flow
            .map { Success(it) as Async<T> }
            .onStart { emit(Loading()) }
            .catch { emit(Fail(it)) }
            .collect { item ->
                withContext(dispatchers.main) {
                    // todo remove once safe
                    state.value = item
                }
            }
    }
    return state.value
}

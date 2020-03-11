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

package com.ivianuu.essentials.android.ui.coroutines

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.onCommit
import androidx.compose.onDispose
import androidx.compose.remember
import androidx.compose.state
import androidx.compose.staticAmbientOf
import androidx.ui.core.CoroutineContextAmbient
import com.ivianuu.essentials.android.ui.injekt.inject
import com.ivianuu.essentials.app.AppComponentHolder
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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
    remember { Job() + AppComponentHolder.component.get<AppCoroutineDispatchers>().main }

@BuilderInference
@Composable
fun <T> launch(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> T
): T? = launch(
    initial = null,
    inputs = *inputs,
    block = block
)

@Composable
fun <T> launch(
    vararg inputs: Any?,
    initial: T,
    block: suspend CoroutineScope.() -> T
): T {
    val state = state { initial }
    val coroutineScope = CoroutineScopeAmbient.current
    val dispatchers = inject<AppCoroutineDispatchers>()
    onCommit(*inputs) {
        val job = coroutineScope.launch {
            val result = block()
            withContext(dispatchers.main) { // todo remove once safe
                state.value = result
            }
        }
        onDispose { job.cancel() }
    }
    return state.value
}

@Composable
fun <T> collect(flow: Flow<T>): T? = collect(flow, null)

@Composable
fun <T> collect(
    flow: Flow<T>,
    initial: T
): T {
    val state = state { initial }
    val dispatchers = inject<AppCoroutineDispatchers>()
    launch(flow) {
        flow
            .collect { item ->
                withContext(dispatchers.main) { // todo remove once safe
                    state.value = item
                }
            }
    }
    return state.value
}

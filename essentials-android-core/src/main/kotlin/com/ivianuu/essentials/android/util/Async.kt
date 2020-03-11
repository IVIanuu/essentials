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

package com.ivianuu.essentials.android.util

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.remember
import androidx.compose.state
import com.ivianuu.essentials.android.ui.coroutines.collect
import com.ivianuu.essentials.android.ui.coroutines.launch
import com.ivianuu.essentials.android.ui.injekt.inject
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

@Immutable
sealed class Async<out T>(val complete: Boolean, val shouldLoad: Boolean) {
    open operator fun invoke(): T? = null
}

@Immutable
object Uninitialized : Async<Nothing>(complete = false, shouldLoad = true)

@Immutable
class Loading<out T> : Async<T>(complete = false, shouldLoad = false) {
    override fun equals(other: Any?) = other is Loading<*>
    override fun hashCode() = "Loading".hashCode()
}

@Immutable
data class Success<out T>(val value: T) : Async<T>(complete = true, shouldLoad = false) {
    override operator fun invoke(): T = value
}

@Immutable
data class Fail<out T>(val error: Throwable) : Async<T>(complete = true, shouldLoad = true)

fun <T> Flow<T>.executeAsync(): Flow<Async<T>> {
    return this
        .map { Success(it) as Async<T> }
        .onStart { emit(Loading()) }
        .catch { Fail<T>(it) }
}

fun <T> Deferred<T>.executeAsync(): Flow<Async<T>> {
    return flow {
        emit(Loading<T>())
        try {
            emit(Success(await()))
        } catch (e: Throwable) {
            emit(Fail<T>(e))
        }
    }
}

fun <T> executeAsync(block: suspend () -> T): Flow<Async<T>> {
    return flow {
        emit(Loading<T>())
        try {
            emit(Success(block()))
        } catch (e: Throwable) {
            emit(Fail<T>(e))
        }
    }
}

inline fun <T, R> Async<T>.map(transform: (T) -> R) =
    if (this is Success) Success(transform(value)) else this as Async<R>

fun <T> Async<T>.valueOrThrow(): T {
    if (this is Success) return value
    else error("$this has no value")
}

@Composable
fun <T> collectAsync(flow: Flow<T>) = collect(
    flow = remember(flow) { flow.executeAsync() },
    initial = Uninitialized
)

@Composable
fun <T> launchAsync(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> T
): Async<T> {
    val state = state<Async<T>> { Uninitialized }
    val dispatchers = inject<AppCoroutineDispatchers>()
    launch(*inputs) {
        state.value = withContext(dispatchers.main) { // todo remove once safe
            Loading()
        }
        try {
            val result = block()
            withContext(dispatchers.main) { // todo remove once safe
                state.value = Success(result)
            }
        } catch (e: Exception) {
            withContext(dispatchers.main) { // todo remove once safe
                state.value = Fail(e)
            }
        }
    }
    return state.value
}

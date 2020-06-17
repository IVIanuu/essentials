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

package com.ivianuu.essentials.util

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.collectAsState
import androidx.compose.remember
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.ivianuu.essentials.ui.coroutines.launchWithState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Immutable
sealed class Async<out T>(
    val complete: Boolean,
    val shouldLoad: Boolean
) {
    open operator fun invoke(): T? = null
}

@Immutable
class Uninitialized<T> : Async<T>(complete = false, shouldLoad = true) {
    override fun toString(): String = "Uninitialized"
}

@Immutable
class Loading<T> : Async<T>(complete = false, shouldLoad = false) {
    override fun equals(other: Any?) = other is Loading<*>
    override fun hashCode() = "Loading".hashCode()
    override fun toString(): String = "Loading"
}

@Immutable
class Success<T>(val value: T) : Async<T>(complete = true, shouldLoad = false) {

    override operator fun invoke(): T = value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Success<*>

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = "Success(value=$value)"

}

@Immutable
class Fail<T>(val error: Throwable) : Async<T>(complete = true, shouldLoad = true) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Fail<*>

        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int = error.hashCode()

    override fun toString(): String = "Error(value=$error)"

}

fun <T> Flow<T>.flowAsync(): Flow<Async<T>> {
    return this
        .map { Success(it) as Async<T> }
        .onStart { emit(Loading()) }
        .catch { Fail<T>(it) }
}

@JvmName("flowAsyncFromResult")
fun <V> Flow<Result<V, Throwable>>.flowAsync(): Flow<Async<V>> {
    return unwrap()
        .flowAsync()
}

fun <T> asyncFlowOf(block: suspend () -> T): Flow<Async<T>> {
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

@Composable
fun <T> Flow<T>.collectAsAsync(): Async<T> {
    return remember(this) { flowAsync() }
        .collectAsState(Uninitialized())
        .value
}

// todo remove overload once compose is fixed
@Composable
fun <T> launchAsync(
    block: suspend CoroutineScope.() -> T
): Async<T> = launchAsync(inputs = *emptyArray(), block = block)

@Composable
fun <T> launchAsync(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> T
): Async<T> {
    return launchWithState<Async<T>>(initial = Uninitialized(), inputs = *inputs) {
        state.value = Loading()
        try {
            val result = block()
            state.value = Success(result)
        } catch (e: Exception) {
            state.value = Fail(e)
        }
    }.value
}

fun <V> Result<V, Throwable>.toAsync(): Async<V> = fold(
    success = { Success(it) },
    failure = { Fail(it) }
)

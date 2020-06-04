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
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import com.ivianuu.essentials.ui.coroutines.collectAsState
import com.ivianuu.essentials.ui.coroutines.launchWithState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Immutable
sealed class Async<T>(
    complete: Boolean,
    shouldLoad: Boolean
) {
    val complete by mutableStateOf(complete)
    val shouldLoad by mutableStateOf(shouldLoad)
    private var readCount by mutableStateOf(0)

    open operator fun invoke(): T? {
        readCount++
        return null
    }

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
class Success<T>(value: T) : Async<T>(complete = true, shouldLoad = false) {

    val value by mutableStateOf(value)

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
class Fail<T>(error: Throwable) : Async<T>(complete = true, shouldLoad = true) {

    val error by mutableStateOf(error)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Fail<*>

        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int = error.hashCode()

    override fun toString(): String = "Success(value=$error)"
}

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

@Composable
fun <T> Flow<T>.collectAsAsync(): Async<T> {
    return remember(this) { executeAsync() }
        .collectAsState(Uninitialized())
        .value
}

@Composable
fun <T> launchAsync(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> T
): Async<T> {
    return launchWithState<Async<T>>(*inputs, initial = Uninitialized()) {
        state.value = Loading()
        try {
            val result = block()
            state.value = Success(result)
        } catch (e: Exception) {
            state.value = Fail(e)
        }
    }.value
}

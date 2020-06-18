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

package com.ivianuu.essentials.ui.resource

import androidx.compose.Composable
import androidx.compose.FrameManager
import androidx.compose.Immutable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.compose.launchInComposition
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.stateFor
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.ivianuu.essentials.util.unwrap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Immutable
sealed class Resource<out T>

@Immutable
object Idle : Resource<Nothing>() {
    override fun toString(): String = "Idle"
}

@Immutable
object Loading : Resource<Nothing>() {
    override fun toString(): String = "Loading"
}

@Immutable
class Success<T>(val value: T) : Resource<T>() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Success<*>

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = "Ok(value=$value)"

}

@Immutable
class Error<T>(val error: Throwable) : Resource<T>() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Error<*>

        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int = error.hashCode()

    override fun toString(): String = "Err(value=$error)"

}

operator fun <T> Resource<T>.invoke(): T? = (this as? Success)?.value

operator fun <T> Success<T>.invoke(): T = value

val Resource<*>.shouldLoad: Boolean get() = this is Idle || this is Error

val Resource<*>.isComplete: Boolean get() = this is Success || this is Error

fun <T> Flow<T>.flowAsResource(): Flow<Resource<T>> {
    return this
        .map { Success(it) as Resource<T> }
        .onStart { emit(Loading) }
        .catch { Error<T>(it) }
}

@JvmName("flowAsyncFromResult")
fun <V> Flow<Result<V, Throwable>>.flowAsResource(): Flow<Resource<V>> {
    return unwrap()
        .flowAsResource()
}

fun <T> resourceFlowOf(block: suspend () -> T): Flow<Resource<T>> {
    return flow {
        emit(Loading)
        try {
            emit(Success(block()))
        } catch (e: Throwable) {
            emit(Error<T>(e))
        }
    }
}

@Composable
fun <T> Flow<T>.collectAsResource(): Resource<T> {
    return remember(this) { flowAsResource() }
        .collectAsState(Idle)
        .value
}

// todo remove overload once compose is fixed
@Composable
fun <T> produceResource(
    block: suspend CoroutineScope.() -> T
): Resource<T> =
    produceResource(
        inputs = *emptyArray(),
        block = block
    )

@Composable
fun <T> produceResource(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> T
): Resource<T> {
    var state by stateFor<Resource<T>>(*inputs) { Idle }

    launchInComposition(*inputs) {
        FrameManager.framed { state = Loading }
        val result = try {
            Success(block())
        } catch (e: Exception) {
            Error(e)
        }

        FrameManager.framed { state = result }
    }

    return state
}

fun <V> Result<V, Throwable>.toResource(): Resource<V> = fold(
    success = { Success(it) },
    failure = { Error(it) }
)

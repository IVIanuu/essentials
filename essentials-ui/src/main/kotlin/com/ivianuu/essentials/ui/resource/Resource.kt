/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import com.ivianuu.essentials.result.Result
import com.ivianuu.essentials.result.fold
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.essentials.store.StateScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

sealed class Resource<out T> {
    open operator fun invoke(): T? = null
}

object Idle : Resource<Nothing>() {
    override fun toString(): String = "Idle"
}

object Loading : Resource<Nothing>() {
    override fun toString(): String = "Loading"
}

class Success<T>(val value: T) : Resource<T>() {
    override fun invoke() = value

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

class Error(val error: Throwable) : Resource<Nothing>() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Error

        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int = error.hashCode()

    override fun toString(): String = "Err(value=$error)"
}

val Resource<*>.shouldLoad: Boolean get() = this is Idle || this is Error

val Resource<*>.isComplete: Boolean get() = this is Success || this is Error

inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> = when (this) {
    is Success -> Success(transform(value))
    else -> this as Resource<R>
}

inline fun <T, R> Resource<T>.flatMap(transform: (T) -> Resource<R>): Resource<R> = when (this) {
    is Success -> transform(value)
    else -> this as Resource<R>
}

fun <T> Flow<T>.flowAsResource(): Flow<Resource<T>> = resourceFlow {
    emitAll(this@flowAsResource)
}

inline fun <T> resource(crossinline block: suspend () -> T): Flow<Resource<T>> {
    return resourceFlow { emit(block()) }
}

fun <S, T> StateScope<S>.reduceResource(
    block: suspend () -> T,
    reducer: S.(Resource<T>) -> S
): Job {
    return resource(block)
        .reduce(reducer)
        .launchIn(this)
}

fun <T, S> Flow<T>.reduceResource(scope: StateScope<S>, reducer: S.(Resource<T>) -> S) = with(scope) {
    flowAsResource().reduce { reducer(it) }
}

fun <T> resourceFlow(@BuilderInference block: suspend FlowCollector<T>.() -> Unit): Flow<Resource<T>> {
    return flow<Resource<T>> {
        emit(Loading)
        runKatching {
            block(object : FlowCollector<T> {
                override suspend fun emit(value: T) {
                    this@flow.emit(Success(value))
                }
            })
        }.onFailure { emit(Error(it)) }
    }
}

@Composable
fun <T> Flow<T>.collectAsResource(): Resource<T> {
    return remember(this) { flowAsResource() }
        .collectAsState(Idle)
        .value
}

@Composable
fun <T> produceResource(
    producer: suspend CoroutineScope.() -> T
): Resource<T> = produceResource<T>(subjects = *emptyArray(), producer = producer)

@Composable
fun <T> produceResource(
    vararg subjects: Any?,
    producer: suspend CoroutineScope.() -> T
): Resource<T> = produceState<Resource<T>>(Idle, *subjects) {
    value = Loading
    value = runKatching { producer() }.toResource()
}.value

inline fun <V> Result<V, Throwable>.toResource(): Resource<V> = fold(
    success = { Success(it) },
    failure = { Error(it) }
)

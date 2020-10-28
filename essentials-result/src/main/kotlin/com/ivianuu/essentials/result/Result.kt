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

package com.ivianuu.essentials.result

sealed class Result<out V, out E>

data class Ok<V>(val value: V) : Result<V, Nothing>()
data class Err<E>(val error: E) : Result<Nothing, E>()

operator fun <V> Result<V, *>.component1(): V? = (this as? Ok)?.value
operator fun <E> Result<*, E>.component2(): E? = (this as? Err)?.error

inline fun <V> runCatching(block: () -> V): Result<V, Throwable> = try {
    Ok(block())
} catch (e: Throwable) {
    Err(e)
}

inline fun <T, V> T.runCatching(block: T.() -> V): Result<V, Throwable> = try {
    Ok(block())
} catch (e: Throwable) {
    Err(e)
}

inline fun <V, E, W> Result<V, E>.map(transform: (V) -> W): Result<W, E> = when (this) {
    is Ok -> Ok(transform(value))
    is Err -> this
}

inline fun <V, E, F> Result<V, E>.mapError(transform: (E) -> F): Result<V, F> = when (this) {
    is Ok -> this
    is Err -> Err(transform(error))
}

inline fun <V, E, U> Result<V, E>.fold(success: (V) -> U, failure: (E) -> U): U = when (this) {
    is Ok -> success(value)
    is Err -> failure(error)
}

inline fun <V, E, U> Result<V, E>.flatMap(transform: (V) -> Result<U, E>): Result<U, E> =
    when (this) {
        is Ok -> transform(value)
        is Err -> this
    }

fun <V> Result<V, *>.getOrNull(): V? = (this as? Ok)?.value

fun <V> Result<V, *>.get(): V = when (this) {
    is Ok -> value
    is Err -> throw IllegalStateException("Called get() on a Err type $error")
}

inline fun <V, E> Result<V, E>.getOrElse(defaultValue: (E) -> V): V = when (this) {
    is Ok -> value
    is Err -> defaultValue(error)
}

fun <E> Result<*, E>.getErrorOrNull(): E? = (this as? Err)?.error

fun <E> Result<*, E>.getError(): E = when (this) {
    is Ok -> throw IllegalStateException("Called getError() on a Ok type $value")
    is Err -> error
}

inline fun <V, E> Result<V, E>.getErrorOrElse(defaultValue: (V) -> E): E = when (this) {
    is Ok -> defaultValue(value)
    is Err -> error
}

inline fun <V, E> Result<V, E>.recover(transform: (E) -> V): Ok<V> = when (this) {
    is Ok -> this
    is Err -> Ok(transform(error))
}

inline fun <V, E> Result<V, E>.onSuccess(action: (V) -> Unit): Result<V, E> {
    if (this is Ok) action(value)
    return this
}

inline fun <V, E> Result<V, E>.onFailure(action: (E) -> Unit): Result<V, E> {
    if (this is Err) action(error)
    return this
}

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

import androidx.compose.Immutable

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

inline fun <T, R> Async<T>.map(transform: (T) -> R) =
    if (this is Success) Success(transform(value)) else this as Async<R>

fun <T> Async<T>.valueOrThrow(): T {
    if (this is Success) return value
    else error("$this has no value")
}

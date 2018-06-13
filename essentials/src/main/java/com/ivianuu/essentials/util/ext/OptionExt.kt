/*
 * Copyright 2018 Manuel Wrage
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

@file:Suppress("NOTHING_TO_INLINE") // Aliases to other public API.

package com.ivianuu.essentials.util.ext

import com.ivianuu.essentials.util.Option

inline val <T : Any> Option<T>.isSome
    get() = this is Option.Some

fun <T : Any> T?.toOption() = if (this != null) {
    Option.Some(this)
} else {
    Option.None.INSTANCE
}

inline fun <T : Any> optionOf(value: T?) = value.toOption()

inline fun <T : Any> absent() = Option.None.INSTANCE as Option<T>

inline fun <T : Any> Option<T>.get() = if (this is Option.Some) {
    this.value
} else {
    null
}

inline fun <T : Any> Option<T>.getOrDefault(other: T) = get() ?: other

inline fun <T : Any> Option<T>.getOrDefault(other: () -> T) = get() ?: other.invoke()

inline fun <T : Any, X : Throwable> Option<T>.getOrThrow(throwable: X) = get() ?: throw throwable

inline fun <T : Any, X : Throwable> Option<T>.getOrThrow(throwable: () -> X) =
    get() ?: throw throwable.invoke()

inline fun <T : Any> Option<T>.require() =
    get() ?: IllegalStateException("called require but is none")

inline fun <T : Any, U : Any> Option<T>.map(mapper: (T) -> U) = if (this is Option.Some) {
    optionOf(mapper.invoke(value))
} else {
    absent()
}

inline fun <T : Any, U : Any> Option<T>.flatMap(mapper: (T) -> Option<U>) =
    if (this is Option.Some) {
        mapper.invoke(value)
    } else {
        absent()
    }

inline fun <T : Any> Option<T>.filter(predicate: (T) -> Boolean) =
    if (this is Option.Some && predicate.invoke(value)) {
        this
    } else {
        absent<T>()
    }

inline fun <T : Any> Option<T>.ifSome(consumer: (T) -> Unit) {
    if (this is Option.Some) {
        consumer.invoke(value)
    }
}

inline fun <T : Any> Option<T>.ifNone(func: () -> Unit) {
    if (!isSome) func()
}
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

inline fun <V> binding(@BuilderInference block: ResultBinding<V>.() -> V): Result<V, Throwable> {
    val receiver = ResultBindingImpl<V>()
    return try {
        with(receiver) { Ok(block()) }
    } catch (e: BindException) {
        receiver.error
    }
}

internal object BindException : Exception()

interface ResultBinding<V> {
    operator fun <T> Result<T, Throwable>.not(): T
}

@PublishedApi
internal class ResultBindingImpl<V> : ResultBinding<V> {
    lateinit var error: Err<Throwable>

    override fun <T> Result<T, Throwable>.not(): T {
        return when (this) {
            is Ok -> value
            is Err -> {
                this@ResultBindingImpl.error = this
                throw BindException
            }
        }
    }
}

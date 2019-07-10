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

sealed class Async<out T>(val complete: Boolean, val shouldLoad: Boolean) {
    open operator fun invoke(): T? = null
}

object Uninitialized : Async<Nothing>(complete = false, shouldLoad = true), Incomplete

class Loading<out T> : Async<T>(complete = false, shouldLoad = false), Incomplete {
    override fun equals(other: Any?) = other is Loading<*>
    override fun hashCode() = "Loading".hashCode()
}

data class Success<out T>(private val value: T) : Async<T>(complete = true, shouldLoad = false) {
    override operator fun invoke(): T = value
}

fun <T> T.asSuccess(): Success<T> = Success(this)

data class Fail<out T>(val error: Throwable) : Async<T>(complete = true, shouldLoad = true)

fun <T> Throwable.asFail(): Fail<T> = Fail(this)

interface Incomplete
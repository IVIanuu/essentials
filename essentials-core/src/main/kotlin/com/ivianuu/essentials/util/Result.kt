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

package com.ivianuu.essentials.util

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.unwrap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

typealias DefaultResult<V> = Result<V, Throwable>

fun <V> Flow<V>.flowCatching(): Flow<Result<V, Throwable>> {
    return map<V, Result<V, Throwable>> { Ok(it) }
        .catch {
            it.printStackTrace()
            emit(Err(it))
        }
}

fun <V, E> Flow<Result<V, E>>.unwrap(): Flow<V> {
    return map { it.unwrap() }
}

@JvmName("unwrapThrowable")
fun <V> Flow<Result<V, Throwable>>.unwrap(): Flow<V> {
    return transform {
        when (it) {
            is Ok -> emit(it.value)
            is Err -> throw it.error
        }
    }
}

inline fun <V> runCatchingAndLog(block: () -> V): Result<V, Throwable> {
    return try {
        Ok(block())
    } catch (e: Throwable) {
        e.printStackTrace()
        Err(e)
    }
}

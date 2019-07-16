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

package com.ivianuu.essentials.ui.traveler

import com.ivianuu.essentials.util.PublishSubject
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.pop
import com.ivianuu.traveler.push
import kotlinx.coroutines.rx2.awaitFirst

interface ResultKey<T>

suspend fun <T> Router.pushForResult(
    key: ResultKey<T>,
    data: Any? = null
): T {
    push(key, data)
    return results
        .filter { it.key == key }
        .awaitFirst()
        .result as T
}

fun <T> Router.popWithResult(key: ResultKey<T>, result: T) {
    pop()
    results.onNext(Result(key, result))
}

private val results =
    PublishSubject<Result<out Any?>>()

internal data class Result<T>(
    val key: ResultKey<T>,
    val result: T
)
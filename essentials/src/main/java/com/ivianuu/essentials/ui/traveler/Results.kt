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
import io.reactivex.Observable
import kotlinx.coroutines.rx2.awaitFirst
import kotlin.reflect.KClass

// todo remove

private data class Result(
    val resultCode: Int,
    val result: Any
)

private val results =
    PublishSubject<Result>()

interface ResultKey<T> {
    var resultCode: Int
}

private var resultCodes = 0

@PublishedApi
internal fun getResultCodes() = ++resultCodes

suspend inline fun <reified T : Any> Router.pushForResult(
    key: ResultKey<T>,
    resultCode: Int = getResultCodes(),
    data: Any? = null
): T {
    key.resultCode = resultCode
    push(key, data)
    return results<T>(resultCode).awaitFirst()
}

inline fun <reified T : Any> Router.results(resultCode: Int): Observable<T> =
    results(T::class, resultCode)

fun <T : Any> Router.results(type: KClass<T>, resultCode: Int): Observable<T> {
    return results
        .filter { it.resultCode == resultCode }
        .map { it.result }
        .ofType(type.java)
}

fun Router.sendResult(resultCode: Int, result: Any) {
    results.onNext(
        Result(
            resultCode,
            result
        )
    )
}

fun Router.popWithResult(resultCode: Int, result: Any) {
    pop()
    sendResult(resultCode, result)
}
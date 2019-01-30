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



package com.ivianuu.essentials.util.ext

import com.ivianuu.rxjavaktx.PublishSubject
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.goBack
import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType

private data class Result(
    val resultCode: Int,
    val result: Any
)

private val results = PublishSubject<Result>()

fun <reified T : Any> Router.results(resultCode: Int): Observable<T> = results
    .filter { it.resultCode == resultCode }
    .map { it.result }
    .ofType()

fun Router.sendResult(resultCode: Int, result: Any) {
    results.onNext(Result(resultCode, result))
}

fun Router.goBackWithResult(resultCode: Int, result: Any) {
    goBack()
    sendResult(resultCode, result)
}
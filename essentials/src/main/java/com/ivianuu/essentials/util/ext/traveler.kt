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

import com.ivianuu.epoxyprefs.PreferenceModel
import com.ivianuu.rxjavaktx.BehaviorSubject
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.goBack
import com.ivianuu.traveler.navigate
import io.reactivex.rxkotlin.ofType

fun PreferenceModel.Builder.onClickKey(router: Router, key: () -> Any) {
    onClick { router.navigate(key()).andTrue() }
}

@PublishedApi
internal data class Result(
    val resultCode: Int,
    val result: Any
)

@PublishedApi
internal val results = BehaviorSubject<Result>()

inline fun <reified T : Any> Router.results(resultCode: Int) = results
    .filter { it.resultCode == resultCode }
    .map { it.result }
    .ofType<T>()

fun Router.sendResult(resultCode: Int, result: Any) {
    results.onNext(Result(resultCode, result))
}

fun Router.goBackWithResult(resultCode: Int, result: Any) {
    goBack()
    sendResult(resultCode, result)
}
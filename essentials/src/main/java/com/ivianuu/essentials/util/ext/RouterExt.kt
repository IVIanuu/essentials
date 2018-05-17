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

import com.ivianuu.essentials.ui.traveler.key.ResultKey
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.result.ResultListener
import io.reactivex.Observable

fun <T : Any> Router.results(resultCode: Int): Observable<T> = Observable.create<T> { e ->
    val listener = object : ResultListener {
        override fun onResult(result: Any) {
            if (!e.isDisposed) {
                e.onNext(result as T)
            }
        }
    }

    e.setCancellable { removeResultListener(resultCode) }

    setResultListener(resultCode, listener)
}

fun <T : Any> Router.navigateToForResult(key: ResultKey): Observable<T> {
    return results<T>(key.resultCode)
        .doOnSubscribe { navigateTo(key) }
}
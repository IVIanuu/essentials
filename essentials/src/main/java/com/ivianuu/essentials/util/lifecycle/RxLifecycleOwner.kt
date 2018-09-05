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

package com.ivianuu.essentials.util.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.ext.onCompleteStub
import com.ivianuu.essentials.util.ext.onErrorStub
import com.ivianuu.essentials.util.ext.onNextStub
import com.ivianuu.essentials.util.ext.subscribeUi
import io.reactivex.*

/**
 * Rx lifecycle owner
 */
interface RxLifecycleOwner : LifecycleOwner {

    fun Completable.subscribeUi(
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ) = subscribeUi(this@RxLifecycleOwner, onError, onComplete)

    fun <T : Any> Flowable<T>.subscribeUi(
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onNext: (T) -> Unit = onNextStub
    ) = subscribeUi(this@RxLifecycleOwner, onError, onComplete, onNext)

    fun <T : Any> Maybe<T>.subscribeUi(
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onSuccess: (T) -> Unit = onNextStub
    ) = subscribeUi(this@RxLifecycleOwner, onError, onComplete, onSuccess)

    fun <T : Any> Observable<T>.subscribeUi(
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onNext: (T) -> Unit = onNextStub
    ) = subscribeUi(this@RxLifecycleOwner, onError, onComplete, onNext)

    fun <T : Any> Single<T>.subscribeUi(
        onError: (Throwable) -> Unit = onErrorStub,
        onSuccess: (T) -> Unit = onNextStub
    ) = subscribeUi(this@RxLifecycleOwner, onError, onSuccess)

}
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

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.ivianuu.essentials.util.ext.*
import io.reactivex.*
import io.reactivex.rxkotlin.subscribeBy

/**
 * Extensions for lifecycle owner
 */
interface LifecycleOwner2 : LifecycleOwner {

    fun <T> LiveData<T>.observe(onChanged: (T) -> Unit) {
        observeK(this@LifecycleOwner2, onChanged)
    }

    fun <T> LiveEvent<T>.consume(consumer: (T) -> Unit) {
        consume(this@LifecycleOwner2, consumer)
    }

    fun Completable.subscribeUi(
        event: Lifecycle.Event = lifecycle.correspondingEvent(),
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ) = observeOn(MAIN).subscribeBy(onError, onComplete).disposedWith(this@LifecycleOwner2, event)

    fun <T : Any> Flowable<T>.subscribeUi(
        event: Lifecycle.Event = lifecycle.correspondingEvent(),
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onNext: (T) -> Unit = onNextStub
    ) = observeOn(MAIN).subscribeBy(onError, onComplete, onNext).disposedWith(
        this@LifecycleOwner2,
        event
    )

    fun <T : Any> Maybe<T>.subscribeUi(
        event: Lifecycle.Event = lifecycle.correspondingEvent(),
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onSuccess: (T) -> Unit = onNextStub
    ) = observeOn(MAIN).subscribeBy(
        onError,
        onComplete,
        onSuccess
    ).disposedWith(this@LifecycleOwner2, event)

    fun <T : Any> Observable<T>.subscribeUi(
        event: Lifecycle.Event = lifecycle.correspondingEvent(),
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onNext: (T) -> Unit = onNextStub
    ) = observeOn(MAIN).subscribeBy(onError, onComplete, onNext).disposedWith(
        this@LifecycleOwner2,
        event
    )

    fun <T : Any> Single<T>.subscribeUi(
        event: Lifecycle.Event = lifecycle.correspondingEvent(),
        onError: (Throwable) -> Unit = onErrorStub,
        onSuccess: (T) -> Unit = onNextStub
    ) = observeOn(MAIN).subscribeBy(onError, onSuccess).disposedWith(this@LifecycleOwner2, event)
}
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

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.ivianuu.essentials.util.SingleLiveEvent
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.atomic.AtomicReference

fun <T> mutableLiveData(initialValue: T? = null) =
    MutableLiveData<T>().apply {
        if (initialValue != null) {
            value = initialValue
        }
    }

fun <T> singleLiveEvent() = SingleLiveEvent<T>()

fun <T> LiveData<T>.observeK(owner: LifecycleOwner, onChanged: (T) -> Unit) {
    observe(owner, Observer<T> { it?.let(onChanged) })
}

fun <T> LiveData<T>.requireValue() = value ?: throw IllegalStateException("value is null")

fun <T : Any> LiveData<T>.toFlowable(strategy: BackpressureStrategy = BackpressureStrategy.LATEST) =
    toObservable().toFlowable(strategy)

fun <T : Any> Flowable<T>.toLiveData() =
    toObservable().toLiveData()

fun <T : Any> LiveData<T>.toObservable(): Observable<T> {
    return Observable.create { e ->
        val observer = Observer<T> { t ->
            if (!e.isDisposed) {
                e.onNext(t!!)
            }
        }

        e.setCancellable { removeObserver(observer) }

        observeForever(observer)
    }
}

fun <T : Any> Observable<T>.toLiveData(): LiveData<T> {
    return object : LiveData<T>() {

        private val disposable = AtomicReference<Disposable?>()

        override fun onActive() {
            super.onActive()
            disposable.set(
                subscribeBy(
                    onNext = { postValue(it) },
                    onError = {
                        throw RuntimeException(it)
                    }
                )
            )
        }

        override fun onInactive() {
            super.onInactive()
            disposable.getAndSet(null)?.dispose()
        }
    }
}

fun <T : Any> LiveData<T>.toMaybe(): Maybe<T> =
    toObservable().singleElement()

fun <T : Any> Maybe<T>.toLiveData() =
    toObservable().toLiveData()

fun <T : Any> LiveData<T>.toSingle(): Single<T> =
    toObservable().singleOrError()

fun <T : Any> Single<T>.toLiveData() =
    toObservable().toLiveData()
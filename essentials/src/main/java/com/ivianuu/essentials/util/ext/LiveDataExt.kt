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

@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.util.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.atomic.AtomicReference

// todo replace with overload constructor once available
inline fun <T> mutableLiveData(initialValue: T? = null) =
    MutableLiveData<T>().applyIf(initialValue != null) {
        value = initialValue
    }

fun <T> LiveData<T>.observeK(owner: LifecycleOwner, onChanged: (T) -> Unit) {
    observe(owner, Observer<T> { it?.let(onChanged) })
}

inline fun <T> LiveData<T>.requireValue() = value ?: throw IllegalStateException("value is null")

inline fun <T : Any> LiveData<T>.toFlowable(strategy: BackpressureStrategy = BackpressureStrategy.LATEST): Flowable<T> =
    toObservable().toFlowable(strategy)

inline fun <T : Any> Flowable<T>.toLiveData() =
    toObservable().toLiveData()

fun <T : Any> LiveData<T>.toObservable(): Observable<T> = Observable.create { e ->
    val observer = Observer<T> { t ->
        if (!e.isDisposed) {
            e.onNext(t!!)
        }
    }

    e.setCancellable { removeObserver(observer) }

    observeForever(observer)
}

fun <T : Any> Observable<T>.toLiveData(): LiveData<T> = object : LiveData<T>() {

    private val disposable = AtomicReference<Disposable?>()

    override fun onActive() {
        super.onActive()
        disposable.set(
            subscribeBy(
                onNext = { postValue(it) },
                onError = { throw RuntimeException(it) }
            )
        )
    }

    override fun onInactive() {
        super.onInactive()
        disposable.getAndSet(null)?.dispose()
    }
}

inline fun <T : Any> LiveData<T>.toMaybe(): Maybe<T> =
    toObservable().singleElement()

inline fun <T : Any> Maybe<T>.toLiveData() =
    toObservable().toLiveData()

inline fun <T : Any> LiveData<T>.toSingle(): Single<T> =
    toObservable().singleOrError()

inline fun <T : Any> Single<T>.toLiveData() =
    toObservable().toLiveData()
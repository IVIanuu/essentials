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
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.Observer
import com.snakydesign.livedataextensions.filter
import io.reactivex.*

fun <T : Any> MutableLiveData(initialValue: T) =
    android.arch.lifecycle.MutableLiveData<T>().apply { value = initialValue }

fun <T : Any> LiveData<T>.observeK(owner: LifecycleOwner, onChanged: (T) -> Unit) {
    observe(owner, Observer<T> { it?.let(onChanged) })
}

fun <T : Any> LiveData<T>.filter(predicate: (T) -> Boolean) = filter { predicate(it!!) }

fun <T : Any> LiveData<T>.toFlowable(lifecycle: LifecycleOwner) =
    Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycle, this))

fun <T : Any> Flowable<T>.toLiveData() =
        LiveDataReactiveStreams.fromPublisher(this)

fun <T : Any> LiveData<T>.toObservable(lifecycle: LifecycleOwner) =
        toFlowable(lifecycle).toObservable()

fun <T : Any> Observable<T>.toLiveData(strategy: BackpressureStrategy = BackpressureStrategy.LATEST) =
        toFlowable(strategy).toLiveData()

fun <T : Any> LiveData<T>.toMaybe(lifecycle: LifecycleOwner) =
        toFlowable(lifecycle).singleElement()

fun <T : Any> Maybe<T>.toLiveData() =
        toFlowable().toLiveData()

fun <T : Any> LiveData<T>.toSingle(lifecycle: LifecycleOwner) =
        toFlowable(lifecycle).singleOrError()

fun <T : Any> Single<T>.toLiveData() =
        toFlowable().toLiveData()
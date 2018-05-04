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

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

private val onCompleteStub: () -> Unit = {}
private val onNextStub: (Any) -> Unit = {}
private val onErrorPrint: (Throwable) -> Unit = Throwable::printStackTrace

fun Completable.main(): Completable = observeOn(AndroidSchedulers.mainThread())
fun <T : Any> Flowable<T>.main(): Flowable<T> = observeOn(AndroidSchedulers.mainThread())
fun <T : Any> Maybe<T>.main(): Maybe<T> = observeOn(AndroidSchedulers.mainThread())
fun <T : Any> Observable<T>.main(): Observable<T> = observeOn(AndroidSchedulers.mainThread())

fun <T : Any> Single<T>.main(): Single<T> = observeOn(AndroidSchedulers.mainThread())

fun Completable.subscribeAndPrint(onComplete: () -> Unit = onCompleteStub) =
        subscribeBy(onComplete = onComplete, onError = onErrorPrint)

fun <T : Any> Flowable<T>.subscribeAndPrint(onNext: (T) -> Unit = onNextStub) =
    subscribeBy(onNext = onNext, onError = onErrorPrint)

fun <T : Any> Maybe<T>.subscribeAndPrint(onSuccess: (T) -> Unit = onNextStub) =
    subscribeBy(onSuccess = onSuccess, onError = onErrorPrint)

fun <T : Any> Observable<T>.subscribeAndPrint(onNext: (T) -> Unit = onNextStub) =
    subscribeBy(onNext = onNext, onError = onErrorPrint)

fun <T : Any> Single<T>.subscribeAndPrint(onSuccess: (T) -> Unit = onNextStub) =
    subscribeBy(onSuccess = onSuccess, onError = onErrorPrint)

fun <T : Any> BehaviorSubject<T>.requireValue() = value ?: throw IllegalStateException("value is null")

fun <T : Any> behaviorSubject(defaultValue: T? = null): BehaviorSubject<T> = if (defaultValue != null) {
    BehaviorSubject.createDefault(defaultValue)
} else {
    BehaviorSubject.create()
}
fun <T : Any> publishSubject(): PublishSubject<T> = PublishSubject.create<T>()

object RxJavaDisposePlugins {
    var DEFAULT_DISPOSE_EVENT = Lifecycle.Event.ON_DESTROY
}

fun Disposable.disposedWith(
    owner: LifecycleOwner,
    event: Lifecycle.Event = RxJavaDisposePlugins.DEFAULT_DISPOSE_EVENT) {

    owner.lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
            if (event == Lifecycle.Event.ON_CREATE) {
                removeObserverAndDispose(owner)
            }
        }

        override fun onStart(owner: LifecycleOwner) {
            if (event == Lifecycle.Event.ON_START) {
                removeObserverAndDispose(owner)
            }
        }

        override fun onResume(owner: LifecycleOwner) {
            if (event == Lifecycle.Event.ON_RESUME) {
                removeObserverAndDispose(owner)
            }
        }

        override fun onPause(owner: LifecycleOwner) {
            if (event == Lifecycle.Event.ON_PAUSE) {
                removeObserverAndDispose(owner)
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            if (event == Lifecycle.Event.ON_STOP) {
                removeObserverAndDispose(owner)
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                removeObserverAndDispose(owner)
            }
        }

        fun removeObserverAndDispose(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            dispose()
        }
    })
}
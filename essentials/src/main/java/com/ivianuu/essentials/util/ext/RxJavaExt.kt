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

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.tuples.*
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.functions.*
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.*

internal val onSubscribeStub: (Disposable) -> Unit = {}
internal val onCompleteStub: () -> Unit = {}
internal val onNextStub: (Any) -> Unit = {}
internal val onErrorStub: (Throwable) -> Unit =
    { RxJavaPlugins.onError(OnErrorNotImplementedException(it)) }

inline val COMPUTATION get() = Schedulers.computation()
inline val IO get() = Schedulers.io()
inline val MAIN: Scheduler get() = AndroidSchedulers.mainThread()

inline fun <T : Any> BehaviorSubject<T>.requireValue() =
    value ?: throw IllegalStateException("value is null")

inline fun <T : Any> behaviorSubject(defaultValue: T? = null): BehaviorSubject<T> =
    if (defaultValue != null) {
        BehaviorSubject.createDefault(defaultValue)
    } else {
        BehaviorSubject.create()
    }

inline fun completableSubject(): CompletableSubject = CompletableSubject.create()

inline fun <T : Any> maybeSubject(): MaybeSubject<T> = MaybeSubject.create()

inline fun <T : Any> publishSubject(): PublishSubject<T> = PublishSubject.create()

inline fun <T : Any> singleSubject(): SingleSubject<T> = SingleSubject.create()

inline fun <T1, T2, T3, T4> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>
) =
    Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        Function4 { t1: T1, t2: T2, t3: T3, t4: T4 ->
            Quartet(t1, t2, t3, t4)
        })!!

inline fun <T1, T2, T3, T4, T5> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>
) =
    Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        Function5 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5 ->
            Quintet(t1, t2, t3, t4, t5)
        })!!

inline fun <T1, T2, T3, T4, T5, T6> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>
) =
    Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        source6,
        Function6 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6 ->
            Sextet(t1, t2, t3, t4, t5, t6)
        })!!

inline fun <T1, T2, T3, T4, T5, T6, T7> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    source7: Observable<T7>
) =
    Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        source6,
        source7,
        Function7 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7 ->
            Septet(t1, t2, t3, t4, t5, t6, t7)
        })!!

inline fun <T1, T2, T3, T4, T5, T6, T7, T8> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    source7: Observable<T7>,
    source8: Observable<T8>
) =
    Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        source6,
        source7,
        source8,
        Function8 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8 ->
            Octet(t1, t2, t3, t4, t5, t6, t7, t8)
        })!!

inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    source7: Observable<T7>,
    source8: Observable<T8>,
    source9: Observable<T9>
) =
    Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        source6,
        source7,
        source8,
        source9,
        Function9 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9 ->
            Ennead(t1, t2, t3, t4, t5, t6, t7, t8, t9)
        })!!

fun Completable.subscribeUi(
    owner: LifecycleOwner,
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) = observeOn(MAIN).subscribeBy(onError, onComplete).disposedWith(owner, event)

fun <T : Any> Flowable<T>.subscribeUi(
    owner: LifecycleOwner,
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) = observeOn(MAIN).subscribeBy(onError, onComplete, onNext).disposedWith(owner, event)

fun <T : Any> Maybe<T>.subscribeUi(
    owner: LifecycleOwner,
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) = observeOn(MAIN).subscribeBy(onError, onComplete, onSuccess).disposedWith(owner, event)

fun <T : Any> Observable<T>.subscribeUi(
    owner: LifecycleOwner,
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) = observeOn(MAIN).subscribeBy(onError, onComplete, onNext).disposedWith(owner, event)

fun <T : Any> Single<T>.subscribeUi(
    owner: LifecycleOwner,
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) = observeOn(MAIN).subscribeBy(onError, onSuccess).disposedWith(owner, event)
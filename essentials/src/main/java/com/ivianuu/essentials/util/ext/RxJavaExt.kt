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

import com.ivianuu.autodispose.LifecycleScopeProvider
import com.ivianuu.autodispose.ScopeProvider
import com.ivianuu.autodispose.autoDispose
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.*

private val onCompleteStub: () -> Unit = {}
private val onNextStub: (Any) -> Unit = {}
private val onErrorStub: (Throwable) -> Unit =
    { RxJavaPlugins.onError(OnErrorNotImplementedException(it)) }

fun <T : Any> BehaviorSubject<T>.requireValue() =
    value ?: throw IllegalStateException("value is null")

fun <T : Any> behaviorSubject(defaultValue: T? = null): BehaviorSubject<T> =
    if (defaultValue != null) {
        BehaviorSubject.createDefault(defaultValue)
    } else {
        BehaviorSubject.create()
    }

fun completableSubject(): CompletableSubject = CompletableSubject.create()

fun <T : Any> maybeSubject(): MaybeSubject<T> = MaybeSubject.create()

fun <T : Any> publishSubject(): PublishSubject<T> = PublishSubject.create()

fun <T : Any> singleSubject(): SingleSubject<T> = SingleSubject.create()

fun Completable.subscribeForUi(
    scopeProvider: ScopeProvider,
    onComplete: () -> Unit
) {
    subscribeForUi(scopeProvider = scopeProvider, onError = onErrorStub, onComplete = onComplete)
}

fun Completable.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) {
    observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onComplete = onComplete, onError = onError)
        .autoDispose(scopeProvider)
}

fun <E> Completable.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onComplete: () -> Unit
) {
    subscribeForUi(
        lifecycleScopeProvider = lifecycleScopeProvider, untilEvent = untilEvent,
        onError = onErrorStub, onComplete = onComplete
    )
}

fun <E> Completable.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) {
    observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun <T : Any> Flowable<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onNext: (T) -> Unit
) {
    subscribeForUi(
        scopeProvider = scopeProvider, onError = onErrorStub,
        onComplete = onCompleteStub, onNext = onNext
    )
}

fun <T : Any> Flowable<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(scopeProvider)
}

fun <T : Any, E> Flowable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onNext: (T) -> Unit
) {
    subscribeForUi(
        lifecycleScopeProvider = lifecycleScopeProvider, untilEvent = untilEvent,
        onError = onErrorStub, onComplete = onCompleteStub, onNext = onNext
    )
}

fun <T : Any, E> Flowable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun <T : Any> Maybe<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onSuccess: (T) -> Unit
) {
    subscribeForUi(
        scopeProvider = scopeProvider, onError = onErrorStub,
        onComplete = onCompleteStub, onSuccess = onSuccess
    )
}

fun <T : Any> Maybe<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onSuccess = onSuccess, onError = onError, onComplete = onComplete)
        .autoDispose(scopeProvider)
}

fun <T : Any, E> Maybe<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onSuccess: (T) -> Unit
) {
    subscribeForUi(
        lifecycleScopeProvider = lifecycleScopeProvider, untilEvent = untilEvent,
        onError = onErrorStub, onComplete = onCompleteStub, onSuccess = onSuccess
    )
}

fun <T : Any, E> Maybe<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onSuccess = onSuccess, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun <T : Any> Observable<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onNext: (T) -> Unit
) {
    subscribeForUi(
        scopeProvider = scopeProvider, onError = onErrorStub,
        onComplete = onCompleteStub, onNext = onNext
    )
}

fun <T : Any> Observable<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(scopeProvider)
}

fun <T : Any, E> Observable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onNext: (T) -> Unit
) {
    subscribeForUi(
        lifecycleScopeProvider = lifecycleScopeProvider, untilEvent = untilEvent,
        onError = onErrorStub, onComplete = onCompleteStub, onNext = onNext
    )
}

fun <T : Any, E> Observable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun <T : Any> Single<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onSuccess: (T) -> Unit
) {
    subscribeForUi(scopeProvider = scopeProvider, onError = onErrorStub, onSuccess = onSuccess)
}

fun <T : Any> Single<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onSuccess = onSuccess, onError = onError)
        .autoDispose(scopeProvider)
}

fun <T : Any, E> Single<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onSuccess: (T) -> Unit
) {
    subscribeForUi(
        lifecycleScopeProvider = lifecycleScopeProvider, untilEvent = untilEvent,
        onError = onErrorStub, onSuccess = onSuccess
    )
}

fun <T : Any, E> Single<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onSuccess = onSuccess, onError = onError)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}
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
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

private val onCompleteStub: () -> Unit = {}
private val onNextStub: (Any) -> Unit = {}
private val onErrorStub: (Throwable) -> Unit = { RxJavaPlugins.onError(OnErrorNotImplementedException(it)) }
private val onErrorPrint: (Throwable) -> Unit = Throwable::printStackTrace

fun Completable.main(): Completable {
    return if (!isMainThread) {
        this
    } else {
        observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T : Any> Flowable<T>.main(): Flowable<T> {
    return if (!isMainThread) {
        this
    } else {
        observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T : Any> Maybe<T>.main(): Maybe<T> {
    return if (!isMainThread) {
        this
    } else {
        observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T : Any> Observable<T>.main(): Observable<T> {
    return if (!isMainThread) {
        this
    } else {
        observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T : Any> Single<T>.main(): Single<T> {
    return if (!isMainThread) {
        this
    } else {
        observeOn(AndroidSchedulers.mainThread())
    }
}

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

fun Completable.subscribeAndDispose(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) {
    subscribeBy(onComplete = onComplete, onError = onError)
        .autoDispose(scopeProvider)
}

fun <E> Completable.subscribeAndDispose(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) {
    subscribeBy(onComplete = onComplete, onError = onError)
        .autoDispose(lifecycleScopeProvider)
}

fun <E> Completable.subscribeAndDispose(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) {
    subscribeBy(onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun <T : Any> Flowable<T>.subscribeAndDispose(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(scopeProvider)
}

fun <T : Any, E> Flowable<T>.subscribeAndDispose(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider)
}

fun <T : Any, E> Flowable<T>.subscribeAndDispose(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun <T : Any> Maybe<T>.subscribeAndDispose(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    subscribeBy(onSuccess = onSuccess, onError = onError, onComplete = onComplete)
        .autoDispose(scopeProvider)
}

fun <T : Any, E> Maybe<T>.subscribeAndDispose(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    subscribeBy(onSuccess = onSuccess, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider)
}

fun <T : Any, E> Maybe<T>.subscribeAndDispose(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    subscribeBy(onSuccess = onSuccess, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun <T : Any> Observable<T>.subscribeAndDispose(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(scopeProvider)
}

fun <T : Any, E> Observable<T>.subscribeAndDispose(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider)
}

fun <T : Any, E> Observable<T>.subscribeAndDispose(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun <T : Any> Single<T>.subscribeAndDispose(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    subscribeBy(onSuccess = onSuccess, onError = onError)
        .autoDispose(scopeProvider)
}

fun <T : Any, E> Single<T>.subscribeAndDispose(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    subscribeBy(onSuccess = onSuccess, onError = onError)
        .autoDispose(lifecycleScopeProvider)
}

fun <T : Any, E> Single<T>.subscribeAndDispose(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onSuccess = onSuccess, onError = onError)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun Completable.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) {
    main()
        .subscribeBy(onComplete = onComplete, onError = onError)
        .autoDispose(scopeProvider)
}

fun <E> Completable.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) {
    main()
        .subscribeBy(onComplete = onComplete, onError = onError)
        .autoDispose(lifecycleScopeProvider)
}

fun <E> Completable.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) {
    main()
        .subscribeBy(onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun <T : Any> Flowable<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(scopeProvider)
}

fun <T : Any, E> Flowable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider)
}

fun <T : Any, E> Flowable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun <T : Any> Maybe<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onSuccess = onSuccess, onError = onError, onComplete = onComplete)
        .autoDispose(scopeProvider)
}

fun <T : Any, E> Maybe<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onSuccess = onSuccess, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider)
}

fun <T : Any, E> Maybe<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onSuccess = onSuccess, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun <T : Any> Observable<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(scopeProvider)
}

fun <T : Any, E> Observable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider)
}

fun <T : Any, E> Observable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}

fun <T : Any> Single<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onSuccess = onSuccess, onError = onError)
        .autoDispose(scopeProvider)
}

fun <T : Any, E> Single<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onSuccess = onSuccess, onError = onError)
        .autoDispose(lifecycleScopeProvider)
}

fun <T : Any, E> Single<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .subscribeBy(onSuccess = onSuccess, onError = onError)
        .autoDispose(lifecycleScopeProvider, untilEvent)
}
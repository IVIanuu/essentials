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

import android.view.View
import com.uber.autodispose.*
import com.uber.autodispose.android.ViewScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

private val onCompleteStub: () -> Unit = {}
private val onNextStub: (Any) -> Unit = {}
private val onErrorStub: (Throwable) -> Unit = { RxJavaPlugins.onError(OnErrorNotImplementedException(it)) }
private val onErrorPrint: (Throwable) -> Unit = Throwable::printStackTrace

fun Completable.main(): Completable {
    return andThen {
        if (!isMainThread) {
            Completable.complete().observeOn(AndroidSchedulers.mainThread())
        } else {
            Completable.complete().observeOn(Schedulers.trampoline())
        }
    }
}

fun <T : Any> Flowable<T>.main(): Flowable<T> {
    return flatMap {
        if (!isMainThread) {
            Flowable.just(it).observeOn(AndroidSchedulers.mainThread())
        } else {
            Flowable.just(it).observeOn(Schedulers.trampoline())
        }
    }
}

fun <T : Any> Maybe<T>.main(): Maybe<T> {
    return flatMap {
        if (!isMainThread) {
            Maybe.just(it).observeOn(AndroidSchedulers.mainThread())
        } else {
            Maybe.just(it).observeOn(Schedulers.trampoline())
        }
    }
}

fun <T : Any> Observable<T>.main(): Observable<T> {
    return flatMap {
        if (!isMainThread) {
            Observable.just(it).observeOn(AndroidSchedulers.mainThread())
        } else {
            Observable.just(it).observeOn(Schedulers.trampoline())
        }
    }
}

fun <T : Any> Single<T>.main(): Single<T> {
    return flatMap {
        if (!isMainThread) {
            Single.just(it).observeOn(AndroidSchedulers.mainThread())
        } else {
            Single.just(it).observeOn(Schedulers.trampoline())
        }
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

fun Completable.subscribeForUi(
    view: View,
    onComplete: () -> Unit
) {
    subscribeForUi(scopeProvider = ViewScopeProvider.from(view),
        onError = onErrorStub, onComplete = onComplete)
}

fun Completable.subscribeForUi(
    scopeProvider: ScopeProvider,
    onComplete: () -> Unit
) {
    subscribeForUi(scopeProvider = scopeProvider, onError = onErrorStub, onComplete = onComplete)
}

fun Completable.subscribeForUi(
    view: View,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) {
    subscribeForUi(scopeProvider = ViewScopeProvider.from(view), onError = onError, onComplete = onComplete)
}

fun Completable.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) {
    main()
        .autoDisposable(scopeProvider)
        .subscribeBy(onComplete = onComplete, onError = onError)
}

fun <E> Completable.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onComplete: () -> Unit
) {
    subscribeForUi(lifecycleScopeProvider = lifecycleScopeProvider, onError = onErrorStub, onComplete = onComplete)
}

fun <E> Completable.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) {
    main()
        .autoDisposable(lifecycleScopeProvider)
        .subscribeBy(onComplete = onComplete, onError = onError)
}

fun <E> Completable.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onComplete: () -> Unit
) {
    subscribeForUi(lifecycleScopeProvider = lifecycleScopeProvider, untilEvent = untilEvent,
        onError = onErrorStub, onComplete = onComplete)
}

fun <E> Completable.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
) {
    main()
        .autoDisposable(buildScopeProvider(lifecycleScopeProvider, untilEvent))
        .subscribeBy(onError = onError, onComplete = onComplete)
}

fun <T : Any> Flowable<T>.subscribeForUi(
    view: View,
    onNext: (T) -> Unit
) {
    subscribeForUi(scopeProvider = ViewScopeProvider.from(view), onError = onErrorStub,
        onComplete = onCompleteStub, onNext = onNext)
}

fun <T : Any> Flowable<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onNext: (T) -> Unit
) {
    subscribeForUi(scopeProvider = scopeProvider, onError = onErrorStub,
        onComplete = onCompleteStub, onNext = onNext)
}

fun <T : Any> Flowable<T>.subscribeForUi(
    view: View,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    subscribeForUi(scopeProvider = ViewScopeProvider.from(view), onError = onError,
        onComplete = onComplete, onNext = onNext)
}

fun <T : Any> Flowable<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    main()
        .autoDisposable(scopeProvider)
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
}

fun <T : Any, E> Flowable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onNext: (T) -> Unit
) {
    subscribeForUi(lifecycleScopeProvider = lifecycleScopeProvider, onError = onErrorStub,
        onComplete = onCompleteStub, onNext = onNext)
}

fun <T : Any, E> Flowable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    main()
        .autoDisposable(lifecycleScopeProvider)
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
}

fun <T : Any, E> Flowable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onNext: (T) -> Unit
) {
    subscribeForUi(lifecycleScopeProvider = lifecycleScopeProvider, untilEvent = untilEvent,
        onError = onErrorStub, onComplete = onCompleteStub, onNext = onNext)
}

fun <T : Any, E> Flowable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    main()
        .autoDisposable(buildScopeProvider(lifecycleScopeProvider, untilEvent))
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
}

fun <T : Any> Maybe<T>.subscribeForUi(
    view: View,
    onSuccess: (T) -> Unit
) {
    subscribeForUi(scopeProvider = ViewScopeProvider.from(view), onError = onErrorStub,
        onComplete = onCompleteStub, onSuccess = onSuccess)
}

fun <T : Any> Maybe<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onSuccess: (T) -> Unit
) {
    subscribeForUi(scopeProvider = scopeProvider, onError = onErrorStub,
        onComplete = onCompleteStub, onSuccess = onSuccess)
}

fun <T : Any> Maybe<T>.subscribeForUi(
    view: View,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    subscribeForUi(scopeProvider = ViewScopeProvider.from(view), onError = onError,
        onComplete = onComplete, onSuccess = onSuccess)
}

fun <T : Any> Maybe<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .autoDisposable(scopeProvider)
        .subscribeBy(onSuccess = onSuccess, onError = onError, onComplete = onComplete)
}

fun <T : Any, E> Maybe<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onSuccess: (T) -> Unit
) {
    subscribeForUi(lifecycleScopeProvider = lifecycleScopeProvider, onError = onErrorStub,
        onComplete = onCompleteStub, onSuccess = onSuccess)
}

fun <T : Any, E> Maybe<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .autoDisposable(lifecycleScopeProvider)
        .subscribeBy(onSuccess = onSuccess, onError = onError, onComplete = onComplete)
}

fun <T : Any, E> Maybe<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onSuccess: (T) -> Unit
) {
    subscribeForUi(lifecycleScopeProvider = lifecycleScopeProvider, untilEvent = untilEvent,
        onError = onErrorStub, onComplete = onCompleteStub, onSuccess = onSuccess)
}

fun <T : Any, E> Maybe<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .autoDisposable(buildScopeProvider(lifecycleScopeProvider, untilEvent))
        .subscribeBy(onSuccess = onSuccess, onError = onError, onComplete = onComplete)
}

fun <T : Any> Observable<T>.subscribeForUi(
    view: View,
    onNext: (T) -> Unit
) {
    subscribeForUi(scopeProvider = ViewScopeProvider.from(view), onError = onErrorStub,
        onComplete = onCompleteStub, onNext = onNext)
}

fun <T : Any> Observable<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onNext: (T) -> Unit
) {
    subscribeForUi(scopeProvider = scopeProvider, onError = onErrorStub,
        onComplete = onCompleteStub, onNext = onNext)
}

fun <T : Any> Observable<T>.subscribeForUi(
    view: View,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    subscribeForUi(scopeProvider = ViewScopeProvider.from(view), onError = onError,
        onComplete = onComplete, onNext = onNext)
}

fun <T : Any> Observable<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    main()
        .autoDisposable(scopeProvider)
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
}

fun <T : Any, E> Observable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onNext: (T) -> Unit
) {
    subscribeForUi(lifecycleScopeProvider = lifecycleScopeProvider, onError = onErrorStub,
        onComplete = onCompleteStub, onNext = onNext)
}

fun <T : Any, E> Observable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    main()
        .autoDisposable(lifecycleScopeProvider)
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
}

fun <T : Any, E> Observable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onNext: (T) -> Unit
) {
    subscribeForUi(lifecycleScopeProvider = lifecycleScopeProvider, untilEvent = untilEvent,
        onError = onErrorStub, onComplete = onCompleteStub, onNext = onNext)
}

fun <T : Any, E> Observable<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
) {
    main()
        .autoDisposable(buildScopeProvider(lifecycleScopeProvider, untilEvent))
        .subscribeBy(onNext = onNext, onError = onError, onComplete = onComplete)
}

fun <T : Any> Single<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onSuccess: (T) -> Unit
) {
    subscribeForUi(scopeProvider = scopeProvider, onError = onErrorStub, onSuccess = onSuccess)
}

fun <T : Any> Single<T>.subscribeForUi(
    view: View,
    onSuccess: (T) -> Unit
) {
    subscribeForUi(scopeProvider = ViewScopeProvider.from(view), onError = onErrorStub, onSuccess = onSuccess)
}

fun <T : Any> Single<T>.subscribeForUi(
    view: View,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    subscribeForUi(scopeProvider = ViewScopeProvider.from(view), onError = onError, onSuccess = onSuccess)
}

fun <T : Any> Single<T>.subscribeForUi(
    scopeProvider: ScopeProvider,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .autoDisposable(scopeProvider)
        .subscribeBy(onSuccess = onSuccess, onError = onError)
}

fun <T : Any, E> Single<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onSuccess: (T) -> Unit
) {
    subscribeForUi(lifecycleScopeProvider = lifecycleScopeProvider, onError = onErrorStub,
        onSuccess = onSuccess)
}

fun <T : Any, E> Single<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .autoDisposable(lifecycleScopeProvider)
        .subscribeBy(onSuccess = onSuccess, onError = onError)
}

fun <T : Any, E> Single<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onSuccess: (T) -> Unit
) {
    subscribeForUi(lifecycleScopeProvider = lifecycleScopeProvider, untilEvent = untilEvent,
        onError = onErrorStub, onSuccess = onSuccess)
}

fun <T : Any, E> Single<T>.subscribeForUi(
    lifecycleScopeProvider: LifecycleScopeProvider<E>,
    untilEvent: E,
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
) {
    main()
        .autoDisposable(buildScopeProvider(lifecycleScopeProvider, untilEvent))
        .subscribeBy(onSuccess = onSuccess, onError = onError)
}

fun <T : Any> ObservableSubscribeProxy<T>.subscribeBy(
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
): Disposable {
    return if (onError === onErrorStub && onComplete === onCompleteStub) {
        subscribe(onNext)
    } else {
        subscribe(onNext, onError, onComplete)
    }
}

fun <T : Any> FlowableSubscribeProxy<T>.subscribeBy(
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
): Disposable {
    return if (onError === onErrorStub && onComplete === onCompleteStub) {
        subscribe(onNext)
    } else {
        subscribe(onNext, onError, onComplete)
    }
}

fun <T : Any> SingleSubscribeProxy<T>.subscribeBy(
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
): Disposable {
    return if (onError === onErrorStub) {
        subscribe(onSuccess)
    } else {
        subscribe(onSuccess, onError)
    }
}

fun <T : Any> MaybeSubscribeProxy<T>.subscribeBy(
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onSuccess: (T) -> Unit = onNextStub
): Disposable {
    return if (onError === onErrorStub && onComplete === onCompleteStub) {
        subscribe(onSuccess)
    } else {
        subscribe(onSuccess, onError, onComplete)
    }
}

fun CompletableSubscribeProxy.subscribeBy(
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
): Disposable {
    return if (onError === onErrorStub) {
        subscribe(onComplete)
    } else {
        subscribe(onComplete, onError)
    }
}

private fun <E> buildScopeProvider(
    lifecycleScopeProvider: LifecycleScopeProvider<E>, untilEvent: E) = ScopeProvider {
    lifecycleScopeProvider.lifecycle()
        .filter { it == untilEvent }
        .take(1)
        .singleElement()
}
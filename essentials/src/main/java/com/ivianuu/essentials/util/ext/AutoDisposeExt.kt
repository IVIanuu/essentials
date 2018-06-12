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

@file:Suppress("NOTHING_TO_INLINE") // Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.view.View
import com.uber.autodispose.*
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.android.scope
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins

inline fun Completable.autoDisposable(owner: LifecycleOwner) =
        autoDisposable(owner.scope())

inline fun <T> Flowable<T>.autoDisposable(owner: LifecycleOwner) =
    autoDisposable(owner.scope())

inline fun <T> Maybe<T>.autoDisposable(owner: LifecycleOwner) =
    autoDisposable(owner.scope())

inline fun <T> Observable<T>.autoDisposable(owner: LifecycleOwner) =
        autoDisposable(owner.scope())

inline fun <T> Single<T>.autoDisposable(owner: LifecycleOwner) =
    autoDisposable(owner.scope())

inline fun Completable.autoDisposable(owner: LifecycleOwner, untilEvent: Lifecycle.Event) =
    autoDisposable(AndroidLifecycleScopeProvider.from(owner).toScopeProvider(untilEvent))

inline fun <T> Flowable<T>.autoDisposable(owner: LifecycleOwner, untilEvent: Lifecycle.Event) =
    autoDisposable(AndroidLifecycleScopeProvider.from(owner).toScopeProvider(untilEvent))

inline fun <T> Maybe<T>.autoDisposable(owner: LifecycleOwner, untilEvent: Lifecycle.Event) =
    autoDisposable(AndroidLifecycleScopeProvider.from(owner).toScopeProvider(untilEvent))

inline fun <T> Observable<T>.autoDisposable(owner: LifecycleOwner, untilEvent: Lifecycle.Event) =
    autoDisposable(AndroidLifecycleScopeProvider.from(owner).toScopeProvider(untilEvent))

inline fun <T> Single<T>.autoDisposable(owner: LifecycleOwner, untilEvent: Lifecycle.Event) =
    autoDisposable(AndroidLifecycleScopeProvider.from(owner).toScopeProvider(untilEvent))

inline fun Completable.autoDisposable(view: View) =
    autoDisposable(view.scope())

inline fun <T> Flowable<T>.autoDisposable(view: View) =
    autoDisposable(view.scope())

inline fun <T> Maybe<T>.autoDisposable(view: View) =
    autoDisposable(view.scope())

inline fun <T> Observable<T>.autoDisposable(view: View) =
    autoDisposable(view.scope())

inline fun <T> Single<T>.autoDisposable(view: View) =
    autoDisposable(view.scope())

fun <E> LifecycleScopeProvider<E>.toScopeProvider() = ScopeProvider {
    val currentState = peekLifecycle() ?: throw LifecycleNotStartedException()
    val correspondingEvent = correspondingEvents().apply(currentState)
    lifecycle()
        .filter { it == correspondingEvent }
        .take(1)
        .singleElement()
}

fun <E> LifecycleScopeProvider<E>.toScopeProvider(untilEvent: E) = ScopeProvider {
    lifecycle()
        .filter { it == untilEvent }
        .take(1)
        .singleElement()
}

fun <T, E> Observable<T>.autoDisposable(
    provider: LifecycleScopeProvider<E>,
    untilEvent: E
) = autoDisposable(provider.toScopeProvider(untilEvent))

private val onNextStub: (Any) -> Unit = {}
private val onErrorStub: (Throwable) -> Unit = { RxJavaPlugins.onError(
    OnErrorNotImplementedException(it)
) }
private val onCompleteStub: () -> Unit = {}

fun <T : Any> ObservableSubscribeProxy<T>.subscribeBy(
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
): Disposable {
    return if (onError == onErrorStub && onComplete == onCompleteStub) {
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
    return if (onError == onErrorStub && onComplete == onCompleteStub) {
        subscribe(onNext)
    } else {
        subscribe(onNext, onError, onComplete)
    }
}

fun <T : Any> SingleSubscribeProxy<T>.subscribeBy(
    onError: (Throwable) -> Unit = onErrorStub,
    onSuccess: (T) -> Unit = onNextStub
): Disposable {
    return if (onError == onErrorStub) {
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
    return if (onError == onErrorStub && onComplete == onCompleteStub) {
        subscribe(onSuccess)
    } else {
        subscribe(onSuccess, onError, onComplete)
    }
}

fun CompletableSubscribeProxy.subscribeBy(
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub
): Disposable {
    return if (onError == onErrorStub) {
        subscribe(onComplete)
    } else {
        subscribe(onComplete, onError)
    }
}
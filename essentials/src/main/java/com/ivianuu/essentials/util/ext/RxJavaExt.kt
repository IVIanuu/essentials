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

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
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
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

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.*

val COMPUTATION get() = Schedulers.computation()
val IO get() = Schedulers.io()
val MAIN get() = AndroidSchedulers.mainThread()

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
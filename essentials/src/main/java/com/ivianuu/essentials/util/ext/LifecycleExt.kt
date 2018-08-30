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

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.SimpleLifecycleObserver
import io.reactivex.Observable

fun Lifecycle.doOnAny(action: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) =
    addObserver(onAny = action)

fun Lifecycle.doOnCreate(action: (owner: LifecycleOwner) -> Unit) = addObserver(onCreate = action)

fun Lifecycle.doOnStart(action: (owner: LifecycleOwner) -> Unit) = addObserver(onStart = action)

fun Lifecycle.doOnResume(action: (owner: LifecycleOwner) -> Unit) = addObserver(onResume = action)

fun Lifecycle.doOnPause(action: (owner: LifecycleOwner) -> Unit) = addObserver(onPause = action)

fun Lifecycle.doOnStop(action: (owner: LifecycleOwner) -> Unit) = addObserver(onStop = action)

fun Lifecycle.doOnDestroy(action: (owner: LifecycleOwner) -> Unit) = addObserver(onDestroy = action)

fun Lifecycle.addObserver(
    onAny: ((owner: LifecycleOwner, event: Lifecycle.Event) -> Unit)? = null,
    onCreate: ((owner: LifecycleOwner) -> Unit)? = null,
    onStart: ((owner: LifecycleOwner) -> Unit)? = null,
    onResume: ((owner: LifecycleOwner) -> Unit)? = null,
    onPause: ((owner: LifecycleOwner) -> Unit)? = null,
    onStop: ((owner: LifecycleOwner) -> Unit)? = null,
    onDestroy: ((owner: LifecycleOwner) -> Unit)? = null
): LifecycleObserver {
    val observer = object : SimpleLifecycleObserver() {

        override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
            onAny?.invoke(owner, event)
            super.onAny(owner, event)
        }

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            onCreate?.invoke(owner)
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            onStart?.invoke(owner)
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            onResume?.invoke(owner)
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            onPause?.invoke(owner)
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            onStop?.invoke(owner)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            onDestroy?.invoke(owner)
        }
    }

    addObserver(observer)

    return observer
}

fun Lifecycle.events(): Observable<Lifecycle.Event> = Observable.create { e ->
    val observer = object : SimpleLifecycleObserver() {
        override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
            if (!e.isDisposed) {
                e.onNext(event)
            }

            if (event == Lifecycle.Event.ON_DESTROY) {
                e.onComplete()
            }
        }
    }

    e.setCancellable { removeObserver(observer) }

    if (!e.isDisposed) {
        addObserver(observer)
    }
}

fun Lifecycle.state(): Observable<Lifecycle.State> = events()
    .map { currentState }
    .startWith(currentState)
    .distinctUntilChanged()
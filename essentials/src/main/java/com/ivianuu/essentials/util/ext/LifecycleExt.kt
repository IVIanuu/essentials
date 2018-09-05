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

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.lifecycle.SimpleLifecycleObserver
import io.reactivex.Observable

fun Lifecycle.doOnAny(block: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) =
    addObserver(onAny = block)

fun Lifecycle.doOnCreate(block: (owner: LifecycleOwner) -> Unit) = addObserver(onCreate = block)

fun Lifecycle.doOnStart(block: (owner: LifecycleOwner) -> Unit) = addObserver(onStart = block)

fun Lifecycle.doOnResume(block: (owner: LifecycleOwner) -> Unit) = addObserver(onResume = block)

fun Lifecycle.doOnPause(block: (owner: LifecycleOwner) -> Unit) = addObserver(onPause = block)

fun Lifecycle.doOnStop(block: (owner: LifecycleOwner) -> Unit) = addObserver(onStop = block)

fun Lifecycle.doOnDestroy(block: (owner: LifecycleOwner) -> Unit) = addObserver(onDestroy = block)

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
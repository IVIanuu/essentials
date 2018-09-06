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

package com.ivianuu.essentials.util.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.ext.d
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.atomic.AtomicReference

/**
 * Subscribes and disposes the source observable based on the lifecycle state of the owner
 */
class LifecycleTransformer<T : Any>(private val owner: LifecycleOwner) :
    ObservableTransformer<T, T> {

    override fun apply(upstream: Observable<T>): ObservableSource<T> =
        Observable.create(LifecycleObservableOnSubscribe(owner, upstream))

    private class LifecycleObservableOnSubscribe<T : Any>(
        private val owner: LifecycleOwner,
        private val source: Observable<T>
    ) : ObservableOnSubscribe<T> {

        private val disposable = AtomicReference<Disposable?>()

        override fun subscribe(emitter: ObservableEmitter<T>) {
            val lifecycleObserver = object : ActiveInactiveObserver() {
                override fun onActive(owner: LifecycleOwner) {
                    super.onActive(owner)
                    d { "on active -> subscribe" }
                    disposable.set(
                        source.subscribeBy(
                            onNext = { emitter.onNext(it).apply { d { "on next -> $it" } } },
                            onError = { emitter.onError(it).apply { d { "on error -> $it" } } },
                            onComplete = { emitter.onComplete().apply { d { "on complete" } } }
                        )
                    )
                }

                override fun onInactive(owner: LifecycleOwner) {
                    super.onInactive(owner)
                    disposable.getAndSet(null)?.dispose()?.let {
                        d { "on inactive -> dispose" }
                    }
                }

                override fun onStateChanged(owner: LifecycleOwner, event: Lifecycle.Event) {
                    super.onStateChanged(owner, event)
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        d { "on destroy -> clean up" }
                        disposable.getAndSet(null)?.dispose()
                    }
                }
            }

            emitter.setCancellable {
                d { "disposed -> clean up" }
                owner.lifecycle.removeObserver(lifecycleObserver)
                disposable.getAndSet(null)?.dispose()
            }

            if (!emitter.isDisposed) {
                owner.lifecycle.addObserver(lifecycleObserver)
            }
        }
    }
}
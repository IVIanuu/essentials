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

package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.lifecycle.SimpleLifecycleObserver
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.internal.disposables.DisposableHelper
import io.reactivex.plugins.RxJavaPlugins
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * Lifecycle aware observer
 */
internal class LifecycleAwareObserver<T>(
    owner: LifecycleOwner,
    private val alwaysDeliverLastValueWhenUnlocked: Boolean = false,
    private val sourceObserver: Observer<T>
) : AtomicReference<Disposable>(), Observer<T>, Disposable {

    private val lifecycleObserver = object : SimpleLifecycleObserver() {
        override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
            super.onAny(owner, event)
            updateLock()
            if (event == Lifecycle.Event.ON_DESTROY) {
                this@LifecycleAwareObserver.owner = null
                if (!isDisposed) {
                    dispose()
                }
            }
        }
    }

    private val locked = AtomicBoolean(true)

    private var owner: LifecycleOwner? = owner

    private var lastUndeliveredValue: T? = null
    private var lastValue: T? = null

    constructor(
        owner: LifecycleOwner,
        alwaysDeliverLastValueWhenUnlocked: Boolean = false,
        onSubscribe: (Disposable) -> Unit = onSubscribeStub,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onNext: (T) -> Unit = {}
    ) : this(
        owner,
        alwaysDeliverLastValueWhenUnlocked,
        object : Observer<T> {
            override fun onSubscribe(d: Disposable) {
                onSubscribe.invoke(d)
            }

            override fun onNext(t: T) {
                onNext.invoke(t)
            }

            override fun onError(e: Throwable) {
                onError.invoke(e)
            }

            override fun onComplete() {
                onComplete.invoke()
            }
        }
    )

    override fun onSubscribe(d: Disposable) {
        if (DisposableHelper.setOnce(this, d)) {
            requireOwner().lifecycle.addObserver(lifecycleObserver)
            sourceObserver.onSubscribe(this)
        }
    }

    override fun onNext(t: T) {
        if (!locked.get()) {
            sourceObserver.onNext(t)
        } else {
            lastUndeliveredValue = t
        }

        lastValue = t
    }

    override fun onError(e: Throwable) {
        if (!isDisposed) {
            lazySet(DisposableHelper.DISPOSED)
            sourceObserver.onError(e)
        }
    }

    override fun onComplete() {
        sourceObserver.onComplete()
    }

    override fun dispose() {
        DisposableHelper.dispose(this)
    }

    override fun isDisposed() = get() === DisposableHelper.DISPOSED

    private fun updateLock() {
        if (owner?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.STARTED) == true) {
            unlock()
        } else {
            lock()
        }
    }

    private fun unlock() {
        if (!locked.getAndSet(false)) return
        if (!isDisposed) {
            val valueToDeliverOnUnlock =
                if (alwaysDeliverLastValueWhenUnlocked && lastValue != null) lastValue else lastUndeliveredValue
            lastUndeliveredValue = null
            if (valueToDeliverOnUnlock != null) {
                onNext(valueToDeliverOnUnlock)
            }
        }
    }

    private fun lock() {
        locked.set(true)
    }

    private fun requireOwner(): LifecycleOwner = owner!!

    private companion object {
        private val onSubscribeStub: (Disposable) -> Unit = {}
        private val onCompleteStub: () -> Unit = {}
        private val onErrorStub: (Throwable) -> Unit =
            { RxJavaPlugins.onError(OnErrorNotImplementedException(it)) }
    }
}
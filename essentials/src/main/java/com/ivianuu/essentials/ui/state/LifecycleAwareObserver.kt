package com.ivianuu.essentials.ui.state

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.util.SimpleLifecycleObserver
import com.ivianuu.essentials.util.lifecycle.LifecyclePlugins
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
internal class LifecycleAwareObserver<T : Any>(
    owner: LifecycleOwner,
    private val activeState: Lifecycle.State = LifecyclePlugins.DEFAULT_ACTIVE_STATE,
    private val alwaysDeliverLastValueWhenUnlocked: Boolean = false,
    private val sourceObserver: Observer<T>
) : AtomicReference<Disposable>(), Observer<T>, Disposable {

    private val lifecycleObserver = object : SimpleLifecycleObserver() {

        override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
            super.onAny(owner, event)
            updateLock()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            requireOwner().lifecycle.removeObserver(this)
            this@LifecycleAwareObserver.owner = null
            if (!isDisposed) {
                dispose()
            }
        }
    }

    private val locked = AtomicBoolean(true)

    private var owner: LifecycleOwner? = owner

    private var lastUndeliveredValue: T? = null
    private var lastValue: T? = null

    constructor(
        owner: LifecycleOwner,
        activeState: Lifecycle.State = LifecyclePlugins.DEFAULT_ACTIVE_STATE,
        alwaysDeliverLastValueWhenUnlocked: Boolean = false,
        onSubscribe: (Disposable) -> Unit = onSubscribeStub,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onNext: (T) -> Unit = onNextStub
    ) : this(
        owner,
        activeState,
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

    override fun isDisposed(): Boolean {
        return get() === DisposableHelper.DISPOSED
    }

    private fun updateLock() {
        if (owner?.lifecycle?.currentState?.isAtLeast(activeState) == true) {
            unlock()
        } else {
            lock()
        }
    }

    private fun unlock() {
        if (!locked.getAndSet(false)) {
            return
        }
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
}

private val onSubscribeStub: (Disposable) -> Unit = {}
private val onCompleteStub: () -> Unit = {}
private val onNextStub: (Any) -> Unit = {}
private val onErrorStub: (Throwable) -> Unit =
    { RxJavaPlugins.onError(OnErrorNotImplementedException(it)) }
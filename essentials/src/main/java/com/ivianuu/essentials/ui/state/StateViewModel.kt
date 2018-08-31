package com.ivianuu.essentials.ui.state

import android.arch.lifecycle.LifecycleOwner
import com.ivianuu.essentials.ui.common.BaseViewModel
import com.ivianuu.essentials.util.ext.MAIN
import com.uber.autodispose.autoDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * State view model
 */
abstract class StateViewModel<S : Any>(initialState: S? = null) : BaseViewModel() {

    private val backgroundScheduler = Schedulers.single()

    private val stateStore = StateStore<S>(scopeProvider)

    val state
        get() = stateStore.state

    init {
        if (initialState != null) {
            setInitialState(initialState)
        }
    }

    protected fun setInitialState(initialState: S) {
        stateStore.setInitialState(initialState)
    }

    protected fun withState(block: (S) -> Unit) {
        stateStore.get(block)
    }

    protected fun setState(reducer: S.() -> S) {
        stateStore.set(reducer)
    }

    protected fun subscribe(subscriber: (S) -> Unit): Disposable =
        stateStore.observable
            .observeOn(MAIN)
            .autoDisposable(scopeProvider)
            .subscribe(subscriber)

    fun subscribe(owner: LifecycleOwner, subscriber: (S) -> Unit): Disposable {
        val lifecycleAwareObserver = LifecycleAwareObserver(
            owner,
            alwaysDeliverLastValueWhenUnlocked = true,
            onNext = Consumer<S> { subscriber(it) }
        )

        return stateStore.observable
            .observeOn(MAIN)
            .autoDisposable(scopeProvider)
            .subscribeWith(lifecycleAwareObserver)
    }
}
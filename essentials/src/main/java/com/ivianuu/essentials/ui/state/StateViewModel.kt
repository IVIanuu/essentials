package com.ivianuu.essentials.ui.state

import android.arch.lifecycle.LifecycleOwner
import com.ivianuu.essentials.ui.common.BaseViewModel
import com.ivianuu.essentials.util.ext.MAIN
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

/**
 * State view model
 */
abstract class StateViewModel<S : Any>(initialState: S? = null) : BaseViewModel() {

    private val backgroundScheduler = Schedulers.single()

    private val stateStore = StateStore<S>()

    val state
        get() = stateStore.state

    init {
        disposables.add(stateStore)

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
            .subscribe(subscriber)
            .addTo(disposables)

    fun subscribe(owner: LifecycleOwner, subscriber: (S) -> Unit): Disposable {
        val lifecycleAwareObserver = LifecycleAwareObserver(
            owner,
            alwaysDeliverLastValueWhenUnlocked = true,
            onNext = Consumer<S> { subscriber(it) }
        )

        return stateStore.observable
            .observeOn(MAIN)
            .subscribeWith(lifecycleAwareObserver)
            .addTo(disposables)
    }
}
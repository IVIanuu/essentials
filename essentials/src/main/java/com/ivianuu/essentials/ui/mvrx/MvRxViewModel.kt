package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.ui.common.BaseViewModel
import com.ivianuu.essentials.util.lifecycle.LifecycleAwareObserver
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.rx2.asObservable

/**
 * State view model
 */
abstract class MvRxViewModel<S : MvRxState>(initialState: S? = null) : BaseViewModel() {

    private val stateStore = MvRxStateStore<S>()

    val state get() = stateStore.state

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
        stateStore.channel
            .asObservable(UI)
            .subscribe(subscriber)
            .addTo(disposables)

    fun subscribe(owner: LifecycleOwner, subscriber: (S) -> Unit): Disposable {
        val lifecycleAwareObserver = LifecycleAwareObserver(
            owner,
            alwaysDeliverLastValueWhenUnlocked = true,
            onNext = subscriber
        )

        return stateStore.channel
            .asObservable(UI)
            .subscribeWith(lifecycleAwareObserver)
            .addTo(disposables)
    }
}
package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.ui.common.BaseViewModel
import com.ivianuu.essentials.ui.common.scope
import com.ivianuu.essentials.util.ext.closeBy
import com.ivianuu.scopes.rx.disposeBy
import com.ivianuu.statestore.StateStore
import com.ivianuu.statestore.rx.observable
import com.ivianuu.timberktx.d
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * State view model
 */
abstract class MvRxViewModel<S : MvRxState>(initialState: S) : BaseViewModel() {

    private val stateStore = StateStore(initialState)
        .closeBy(scope)

    internal val state get() = stateStore.peekState()

    protected fun withState(block: (S) -> Unit) {
        stateStore.withState(block)
    }

    protected fun setState(reducer: S.() -> S) {
        stateStore.setState(reducer)
    }

    fun logStateChanges() {
        subscribe { d { "new state -> $it" } }
    }

    protected fun subscribe(subscriber: (S) -> Unit) =
        stateStore.observable.subscribeLifecycle(null, subscriber)

    fun subscribe(owner: LifecycleOwner, subscriber: (S) -> Unit) =
        stateStore.observable.subscribeLifecycle(owner, subscriber)

    private fun <T> Observable<T>.subscribeLifecycle(
        owner: LifecycleOwner? = null,
        subscriber: (T) -> Unit
    ): Disposable {
        if (owner == null) {
            return subscribe(subscriber).disposeBy(scope)
        }

        val lifecycleAwareObserver = LifecycleAwareObserver(
            owner,
            alwaysDeliverLastValueWhenUnlocked = true,
            onNext = subscriber
        )
        return subscribeWith(lifecycleAwareObserver).disposeBy(scope)
    }

    override fun toString() = "${this::class.java.simpleName} -> $state"
}
package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.closeable.Closeable
import com.ivianuu.closeable.rx.asCloseable
import com.ivianuu.essentials.ui.common.EsViewModel
import com.ivianuu.essentials.ui.common.scope
import com.ivianuu.essentials.util.ext.closeBy
import com.ivianuu.kommon.lifecycle.doOnAny
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.rx.disposeBy
import com.ivianuu.statestore.StateStore
import com.ivianuu.statestore.rx.observable
import com.ivianuu.timberktx.d
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * State view model
 */
abstract class MvRxViewModel<S : MvRxState>(initialState: S) : EsViewModel() {

    private val stateStore = StateStore(initialState).closeBy(scope)

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

    protected fun subscribe(subscriber: (S) -> Unit): Closeable =
        stateStore.observable.subscribe(subscriber).asCloseable()

    fun subscribe(owner: LifecycleOwner, subscriber: (S) -> Unit): Closeable =
        LifecycleStateListener(owner, stateStore, subscriber)

    override fun toString() = "${this::class.java.simpleName} -> $state"
}
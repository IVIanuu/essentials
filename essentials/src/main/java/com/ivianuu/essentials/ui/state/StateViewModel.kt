package com.ivianuu.essentials.ui.state

import com.ivianuu.essentials.ui.common.BaseViewModel
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.autoDisposable

/**
 * State view model
 */
abstract class StateViewModel<S : Any>(initialState: S? = null) : BaseViewModel() {

    private val stateStore = StateStore<S>(scopeProvider)

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

    protected fun subscribe(subscriber: (S) -> Unit) =
        stateStore.observable
            .autoDisposable(scopeProvider)
            .subscribe(subscriber)

    fun subscribe(scopeProvider: ScopeProvider, subscriber: (S) -> Unit) =
        stateStore.observable
            .autoDisposable(scopeProvider)
            .subscribe(subscriber)

}
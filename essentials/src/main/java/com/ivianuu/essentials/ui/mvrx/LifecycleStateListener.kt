package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.closeable.Closeable
import com.ivianuu.essentials.util.SimpleLifecycleObserver
import com.ivianuu.statestore.StateStore

/**
 * @author Manuel Wrage (IVIanuu)
 */
class LifecycleStateListener<T>(
    private val owner: LifecycleOwner,
    private val store: StateStore<T>,
    private val subscriber: (T) -> Unit
) : SimpleLifecycleObserver(), Closeable {

    override var isClosed = false
        private set

    init {
        owner.lifecycle.addObserver(this)
    }

    override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
        super.onAny(owner, event)
        val state = owner.lifecycle.currentState
        when {
            state.isAtLeast(Lifecycle.State.STARTED) -> { store.addStateListener(subscriber) }
            state == Lifecycle.State.DESTROYED -> { close() }
            else -> { store.removeStateListener(subscriber) }
        }
    }

    override fun close() {
        owner.lifecycle.removeObserver(this)
        store.removeStateListener(subscriber)
    }

}
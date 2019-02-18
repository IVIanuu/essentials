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

package com.ivianuu.essentials.ui.mvrx.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ivianuu.closeable.Closeable
import com.ivianuu.essentials.util.SimpleLifecycleObserver
import com.ivianuu.statestore.StateStore

/**
 * Attaches and detaches the [subscriber] to the [store]
 * based on the lifecycle of the [owner]
 * and is also a [Closeable]
 */
internal class LifecycleStateListener<T>(
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
            state.isAtLeast(Lifecycle.State.RESUMED) -> {
                store.addStateListener(subscriber)
            }
            state == Lifecycle.State.DESTROYED -> { close() }
            else -> { store.removeStateListener(subscriber) }
        }
    }

    override fun close() {
        owner.lifecycle.removeObserver(this)
        store.removeStateListener(subscriber)
    }

}
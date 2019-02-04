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

import androidx.lifecycle.LifecycleOwner
import com.ivianuu.closeable.Closeable
import com.ivianuu.closeable.rx.asCloseable
import com.ivianuu.essentials.ui.common.EsViewModel
import com.ivianuu.essentials.ui.common.scope
import com.ivianuu.essentials.ui.mvrx.lifecycle.LifecycleStateListener
import com.ivianuu.essentials.util.ext.closeBy
import com.ivianuu.statestore.StateStore
import com.ivianuu.statestore.rx.observable
import com.ivianuu.timberktx.d

/**
 * State view model
 */
abstract class MvRxViewModel<S>(initialState: S) : EsViewModel() {

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
        stateStore.observable.subscribe(subscriber).asCloseable().closeBy(scope)

    fun subscribe(owner: LifecycleOwner, subscriber: (S) -> Unit): Closeable =
        LifecycleStateListener(
            owner,
            stateStore,
            subscriber
        )

    override fun toString() = "${this::class.java.simpleName} -> $state"
}
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

package com.ivianuu.essentials.ui.viewmodel

import com.ivianuu.essentials.util.SavedState
import com.ivianuu.essentials.util.savedStateOf

/**
 * @author Manuel Wrage (IVIanuu)
 */
abstract class ViewModel {

    private val listeners = mutableSetOf<ViewModelListener>()

    private lateinit var listenerStore: ViewModelListenerStore

    private var superCalled = false

    protected open fun onInitialize(savedState: SavedState?) {
        superCalled = true
    }

    protected open fun onDestroy() {
        superCalled = true
    }

    protected open fun onSaveState(savedState: SavedState) {
        superCalled = true
    }

    fun addListener(listener: ViewModelListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ViewModelListener) {
        listeners.remove(listener)
    }

    internal fun initialize(
        listenerStore: ViewModelListenerStore,
        savedState: SavedState?
    ) {
        this.listenerStore = listenerStore
        notifyListeners { it.preInitialize(this, savedState) }
        requireSuperCalled { onInitialize(savedState) }
        notifyListeners { it.postInitialize(this, savedState) }
    }

    internal fun destroy() {
        notifyListeners { it.preDestroy(this) }
        requireSuperCalled(this::onDestroy)
        notifyListeners { it.postDestroy(this) }
    }

    internal fun saveState(): SavedState {
        val savedState = savedStateOf()
        requireSuperCalled { onSaveState(savedState) }
        notifyListeners { it.onSaveState(this, savedState) }
        return savedState
    }

    private inline fun notifyListeners(block: (ViewModelListener) -> Unit) {
        (listenerStore.getListeners() + listeners.toList()).forEach(block)
    }

    private inline fun requireSuperCalled(block: () -> Unit) {
        superCalled = false
        block()
        check(superCalled) { "super not called" }
    }

}
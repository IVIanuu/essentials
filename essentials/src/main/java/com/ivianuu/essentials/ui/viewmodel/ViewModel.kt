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

/**
 * A view model
 */
abstract class ViewModel {

    private val listeners = mutableListOf<ViewModelListener>()

    private var superCalled = false

    private lateinit var viewModelStore: ViewModelStore

    protected open fun onInit(savedInstanceState: SavedState?) {
    }

    protected open fun onRestoreInstanceState(savedInstanceState: SavedState) {
    }

    protected open fun onSaveInstanceState(outState: SavedState) {
    }

    protected open fun onCleared() {
    }

    fun addViewModelListener(listener: ViewModelListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeViewModelListener(listener: ViewModelListener) {
        listeners.remove(listener)
    }

    internal fun initialize(store: ViewModelStore, savedInstanceState: SavedState?) {
        viewModelStore = store
        notifyListeners { it.preInit(this, savedInstanceState) }
        requireSuperCalled { onInit(savedInstanceState) }
        notifyListeners { it.postInit(this, savedInstanceState) }
    }

    internal fun restoreInstanceState(savedInstanceState: SavedState) {
        requireSuperCalled { onRestoreInstanceState(savedInstanceState) }
        notifyListeners { it.onRestoreInstanceState(this, savedInstanceState) }
    }

    internal fun saveInstanceState(outState: SavedState) {
        requireSuperCalled { onSaveInstanceState(outState) }
        notifyListeners { it.onSaveInstanceState(this, outState) }
    }

    internal fun clear() {
        notifyListeners { it.preCleared(this) }
        requireSuperCalled { onCleared() }
        notifyListeners { it.postCleared(this) }
    }

    private inline fun notifyListeners(action: (ViewModelListener) -> Unit) {
        val allListeners = listeners + viewModelStore.getViewModelListeners()
        allListeners.forEach(action)
    }

    private inline fun requireSuperCalled(block: () -> Unit) {
        superCalled = false
        block()
        if (!superCalled) {
            throw IllegalStateException("super not called")
        }
    }
}
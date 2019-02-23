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

    // todo allow to specify whether to retain instance or not?

    // todo allow to specify restoration mode could be lazily or instant
    // todo this would require a way to construct view models
    // todo so we would need a ViewModelFactory

    private val listeners = mutableSetOf<ViewModelListener>()

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

    internal fun initialize(savedState: SavedState?) {
        notifyListeners { it.preInitialize(this, savedState) }
        requireSuperCalled { onInitialize(savedState) }
        notifyListeners { it.postInitialize(this, savedState) }
    }

    internal fun destroy() {
        notifyListeners { it.preDestroy(this) }
        requireSuperCalled { onDestroy() }
        notifyListeners { it.postDestroy(this) }
    }

    internal fun saveState(): SavedState {
        val savedState = savedStateOf()
        requireSuperCalled { onSaveState(savedState) }
        notifyListeners { it.onSaveState(this, savedState) }
        return savedState
    }

    private inline fun notifyListeners(block: (ViewModelListener) -> Unit) {
        listeners.toList().forEach(block)
    }

    private inline fun requireSuperCalled(block: () -> Unit) {
        superCalled = false
        block()
        check(superCalled) { "super not called" }
    }

}

interface ViewModelListener {
    fun preInitialize(viewModel: ViewModel, savedState: SavedState?) {
    }

    fun postInitialize(viewModel: ViewModel, savedState: SavedState?) {
    }

    fun preDestroy(viewModel: ViewModel) {
    }

    fun postDestroy(viewModel: ViewModel) {
    }

    fun onSaveState(viewModel: ViewModel, savedState: SavedState) {
    }
}
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

import android.os.Bundle

/**
 * @author Manuel Wrage (IVIanuu)
 */
abstract class ViewModel {

    private val listeners = mutableListOf<ViewModelListener>()

    private var superCalled = false

    protected open fun onInit(savedInstanceState: SavedState?) {
    }

    protected open fun onRestoreInstanceState(savedInstanceState: SavedState) {
    }

    protected open fun onSaveInstanceState(outState: SavedState) {
    }

    protected open fun onCleared() {
    }

    fun addListener(listener: ViewModelListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: ViewModelListener) {
        listeners.remove(listener)
    }

    internal fun initialize(savedInstanceState: SavedState?) {
        listeners.toList().forEach { it.preInit(this, savedInstanceState) }
        requireSuperCalled { onInit(savedInstanceState) }
        listeners.toList().forEach { it.postInit(this, savedInstanceState) }
    }

    internal fun restoreInstanceState(savedInstanceState: SavedState) {
        requireSuperCalled { onRestoreInstanceState(savedInstanceState) }
        listeners.toList().forEach { it.onRestoreInstanceState(this, savedInstanceState) }
    }

    internal fun saveInstanceState(outState: SavedState) {
        requireSuperCalled { onSaveInstanceState(outState) }
        listeners.toList().forEach { it.onSaveInstanceState(this, outState) }
    }

    internal fun clear() {
        listeners.toList().forEach { it.preCleared(this) }
        requireSuperCalled { onCleared() }
        listeners.toList().forEach { it.postCleared(this) }
    }

    private inline fun requireSuperCalled(block: () -> Unit) {
        superCalled = false
        block()
        if (!superCalled) {
            throw IllegalStateException("super not called")
        }
    }
}
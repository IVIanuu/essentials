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
import kotlin.reflect.KClass

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ViewModelManager {

    val viewModels: Map<String, ViewModel> get() = _viewModels
    private val _viewModels = mutableMapOf<String, ViewModel>()

    private var viewModelStates = mutableMapOf<String, SavedState>()

    internal val viewModelListeners = mutableSetOf<ViewModelListener>()

    private val listenerStore = ViewModelListenerStore.Manager(this)

    fun <T : ViewModel> get(
        type: KClass<T>,
        key: String = type.defaultViewModelKey,
        factory: () -> T
    ): T {
        var viewModel = _viewModels[key] as? T
        if (viewModel == null) {
            viewModel = factory()
            _viewModels[key] = viewModel
            val savedState = viewModelStates.remove(key)
            viewModel.initialize(listenerStore, savedState)
        }

        return viewModel
    }

    fun restoreState(savedState: SavedState?) {
        if (savedState == null) return
        val viewModelStates = savedState.get<SavedState>(KEY_VIEW_MODELS)!!
        this.viewModelStates.putAll(viewModelStates.entries as Map<out String, SavedState>)
    }

    fun saveState(): SavedState {
        val savedState = savedStateOf()

        val viewModelsStates = savedStateOf()

        _viewModels
            .mapValues { it.value.saveState() }
            .forEach { (key, state) -> viewModelsStates[key] = state }

        savedState[KEY_VIEW_MODELS] = viewModelsStates

        return savedState
    }

    fun destroy() {
        _viewModels.forEach { it.value.destroy() }
        _viewModels.clear()
    }

    fun addViewModelListener(listener: ViewModelListener) {
        viewModelListeners.add(listener)
    }

    fun removeViewModelListener(listener: ViewModelListener) {
        viewModelListeners.remove(listener)
    }

    private companion object {
        private const val KEY_VIEW_MODELS = "ViewModelManager.viewModels"
    }

}

inline fun <reified T : ViewModel> ViewModelManager.get(
    key: String = T::class.defaultViewModelKey,
    noinline factory: () -> T
): T = get(T::class, key, factory)

val <T : ViewModel> KClass<T>.defaultViewModelKey: String
    get() = "ViewModelManager.defaultKey:" + java.canonicalName
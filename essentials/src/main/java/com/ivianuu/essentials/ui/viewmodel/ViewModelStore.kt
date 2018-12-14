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
import kotlin.reflect.KClass

/**
 * Store for [ViewModel]s
 */
open class ViewModelStore {

    val viewModels get() = _viewModels.toMap()
    private val _viewModels = mutableMapOf<String, ViewModel>()

    private var savedState: Bundle? = null

    private val listeners = mutableListOf<ViewModelStoreListener>()

    operator fun <T : ViewModel> get(
        clazz: KClass<T>,
        key: String = clazz.defaultViewModelKey,
        factory: () -> T
    ): T {
        var viewModel = _viewModels[key] as? T

        if (viewModel == null) {
            viewModel = factory()

            _viewModels[key] = viewModel

            val viewModelState = savedState?.getBundle(KEY_VIEW_MODEL_STATE_PREFIX + key)
                ?.let { BundleSavedState(it) }

            listeners.toList().forEach { it.onViewModelAdded(this, viewModel, viewModelState) }

            viewModel.initialize(viewModelState)
            viewModelState?.let { viewModel.restoreInstanceState(it) }
        }

        return viewModel
    }

    fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedState = savedInstanceState
    }

    fun saveInstanceState(): Bundle {
        val bundle = Bundle()

        _viewModels
            .mapValues { (_, vm) ->
                BundleSavedState(Bundle()).also { vm.saveInstanceState(it) }
            }
            .forEach { bundle.putBundle(it.key, it.value.bundle) }

        return bundle
    }

    fun clear() {
        _viewModels.forEach { it.value.clear() }
        _viewModels.clear()
        savedState = null
    }

    fun addListener(listener: ViewModelStoreListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: ViewModelStoreListener) {
        listeners.remove(listener)
    }

    private companion object {
        private const val KEY_VIEW_MODEL_STATE_PREFIX = "ViewModelStore.viewModelState"
    }
}

inline fun <reified T : ViewModel> ViewModelStore.get(
    key: String = T::class.defaultViewModelKey,
    noinline factory: () -> T
) = get(T::class, key, factory)
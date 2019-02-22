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
import android.os.Parcelable
import androidx.core.os.bundleOf
import kotlinx.android.parcel.Parcelize
import kotlin.reflect.KClass

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ViewModelManager {

    val viewModels: Map<String, ViewModel> get() = _viewModels
    private val _viewModels = mutableMapOf<String, ViewModel>()

    private var viewModelStates = mutableMapOf<String, Bundle>()

    private val viewModelListeners = mutableSetOf<ViewModelListener>()

    fun <T : ViewModel> get(
        type: KClass<T>,
        key: String? = null,
        factory: () -> T
    ): T = getInternal(key ?: type.defaultViewModelKey, factory)

    fun <T : ViewModel> getOrNull(type: KClass<T>, key: String? = null): T? =
        _viewModels[key ?: type.defaultViewModelKey] as? T

    fun restoreState(savedState: Bundle?) {
        if (savedState == null) return
        viewModelStates.putAll(
            savedState.getParcelableArrayList<ViewModelState>(KEY_VIEW_MODELS)!!
                .associateBy { it.key }
                .mapValues { it.value.savedState }
        )
    }

    fun saveState(): Bundle {
        val savedState = bundleOf()

        _viewModels
            .mapValues { it.value.saveState() }
            .map { ViewModelState(it.key, it.value) }
            .let { savedState.putParcelableArrayList(KEY_VIEW_MODELS, ArrayList(it)) }

        return savedState
    }

    fun destroy() {
        _viewModels.forEach { it.value.destroy() }
        _viewModels.clear()
    }

    fun addViewModelListener(listener: ViewModelListener) {
        viewModelListeners.add(listener)
        _viewModels.values.forEach { it.addListener(listener) }
    }

    fun removeViewModelListener(listener: ViewModelListener) {
        viewModelListeners.remove(listener)
        _viewModels.values.forEach { it.removeListener(listener) }
    }

    private fun <T : ViewModel> getInternal(key: String, factory: () -> T): T {
        var viewModel = _viewModels[key] as? T
        if (viewModel == null) {
            viewModel = factory()
            val savedState = viewModelStates.remove(key)
            viewModelListeners.forEach { viewModel.addListener(it) }
            viewModel.initialize(savedState)
        }

        return viewModel
    }

    @Parcelize
    private class ViewModelState(
        val key: String,
        val savedState: Bundle
    ) : Parcelable

    private companion object {
        private const val KEY_VIEW_MODELS = "viewmodels"
    }

}

inline fun <reified T : ViewModel> ViewModelManager.get(
    key: String? = null,
    noinline factory: () -> T
): T = get(T::class, key, factory)

inline fun <reified T : ViewModel> ViewModelManager.getOrNull(
    key: String? = null
): T? = getOrNull(T::class, key)

val <T : ViewModel> KClass<T>.defaultViewModelKey
    get() = "ViewModelStore.defaultKey:" + java.canonicalName
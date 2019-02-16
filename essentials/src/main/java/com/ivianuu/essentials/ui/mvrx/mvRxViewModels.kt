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

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.ui.mvrx.lifecycle.lifecycleAwareLazy
import com.ivianuu.kommon.lifecycle.defaultViewModelKey

inline fun <reified VM : MvRxViewModel<*>> MvRxView.viewModel(
    noinline from: () -> ViewModelStoreOwner = { this },
    noinline key: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
): Lazy<VM> = viewModelLazy { getViewModel(from(), key(), factory) }

inline fun <reified VM : MvRxViewModel<*>> MvRxView.getViewModel(
    from: ViewModelStoreOwner = this,
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
): VM = viewModelProvider(from, factory).get(key, VM::class.java)
    .setupViewModel(this)

inline fun <reified VM : MvRxViewModel<*>> MvRxView.existingViewModel(
    noinline from: () -> ViewModelStoreOwner = { this },
    noinline key: () -> String = { VM::class.defaultViewModelKey }
): Lazy<VM> = viewModel(from, key, ExistingViewModelFactory())

inline fun <reified VM : MvRxViewModel<*>> MvRxView.getExistingViewModel(
    from: ViewModelStoreOwner = this,
    key: String = VM::class.defaultViewModelKey
): VM = getViewModel(from, key, ExistingViewModelFactory())

@PublishedApi
internal fun <VM : MvRxViewModel<*>> VM.setupViewModel(view: MvRxView): VM =
    apply { subscribe(view) { view.postInvalidate() } }

@PublishedApi
internal fun <V> MvRxView.viewModelLazy(initializer: () -> V): Lazy<V> =
    lifecycleAwareLazy(Lifecycle.Event.ON_START, initializer)

@PublishedApi
internal fun <VM : MvRxViewModel<*>> viewModelProvider(
    from: ViewModelStoreOwner,
    factory: () -> VM
): ViewModelProvider = ViewModelProvider(from, MvRxViewModelFactory(factory))

@PublishedApi
internal class ExistingViewModelFactory<VM : MvRxViewModel<*>> : () -> VM {
    override fun invoke(): VM {
        error("view model does not exist.")
    }
}

@PublishedApi
internal class MvRxViewModelFactory<VM : MvRxViewModel<*>>(
    private val factory: () -> VM
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = factory() as T
}
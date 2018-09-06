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

@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.util.ext

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.util.viewmodel.ViewModelFactoryHolder
import kotlin.reflect.KClass

inline fun ViewModelStoreOwner.viewModelProvider(
    factory: ViewModelProvider.Factory = defaultViewModelFactory()
) = ViewModelProvider(this, factory)

inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = viewModelProvider(factory).get(key, T::class.java)

inline fun <reified T : ViewModel> ViewModelStoreOwner.bindViewModel(
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = unsafeLazy { viewModel<T>(factoryProvider(), keyProvider()) }

inline fun <reified T : ViewModel> Fragment.activityViewModel(
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = requireActivity().viewModel<T>(factory, key)

inline fun <reified T : ViewModel> Fragment.bindActivityViewModel(
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = unsafeLazy { activityViewModel<T>(factoryProvider(), keyProvider()) }

inline fun <reified T : ViewModel> Fragment.parentViewModel(
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = requireParentFragment().viewModel<T>(factory, key)

inline fun <reified T : ViewModel> Fragment.bindParentViewModel(
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = unsafeLazy { parentViewModel<T>(factoryProvider(), keyProvider()) }

inline fun <reified T : ViewModel> Fragment.targetViewModel(
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = requireTargetFragment().viewModel<T>(factory, key)

inline fun <reified T : ViewModel> Fragment.bindTargetViewModel(
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = unsafeLazy { targetViewModel<T>(factoryProvider(), keyProvider()) }

@PublishedApi
internal inline val KClass<*>.defaultViewModelKey
    get() = "android.arch.lifecycle.ViewModelProvider.DefaultKey:" + java.canonicalName


@PublishedApi
internal inline fun ViewModelStoreOwner.defaultViewModelFactory() =
    if (this is ViewModelFactoryHolder) {
        viewModelFactory
    } else {
        ViewModelProvider.NewInstanceFactory()
    }
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

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStoreOwner
import android.support.v4.app.Fragment

inline fun ViewModelStoreOwner.viewModelProvider(
    factory: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory()
) = ViewModelProvider(this, factory)

inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel() =
    viewModelProvider()[T::class.java]

inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(
    factory: ViewModelProvider.Factory
) = viewModelProvider(factory)[T::class.java]

inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(
    key: String
) = viewModelProvider()[key, T::class.java]

inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(
    key: String,
    factory: ViewModelProvider.Factory
) = viewModelProvider(factory).get(key, T::class.java)

inline fun <reified T : ViewModel> ViewModelStoreOwner.bindViewModel() =
    unsafeLazy { viewModel<T>() }

inline fun <reified T : ViewModel> ViewModelStoreOwner.bindViewModel(
    crossinline factory: () -> ViewModelProvider.Factory
) = unsafeLazy { viewModel<T>(factory()) }

inline fun <reified T : ViewModel> ViewModelStoreOwner.bindViewModel(
    key: String
) = unsafeLazy { viewModel<T>(key) }

inline fun <reified T : ViewModel> Fragment.activityViewModel(): T =
    requireActivity().viewModelProvider()[T::class.java]

inline fun <reified T : ViewModel> Fragment.activityViewModel(
    factory: ViewModelProvider.Factory
) = requireActivity().viewModelProvider(factory)[T::class.java]

inline fun <reified T : ViewModel> Fragment.activityViewModel(
    key: String
) = requireActivity().viewModelProvider()[key, T::class.java]

inline fun <reified T : ViewModel> Fragment.activityViewModel(
    key: String,
    factory: ViewModelProvider.Factory
) = requireActivity().viewModelProvider(factory).get(key, T::class.java)

inline fun <reified T : ViewModel> Fragment.bindActivityViewModel() =
    unsafeLazy { activityViewModel<T>() }

inline fun <reified T : ViewModel> Fragment.bindActivityViewModel(
    crossinline factory: () -> ViewModelProvider.Factory
) = unsafeLazy { activityViewModel<T>(factory()) }

inline fun <reified T : ViewModel> Fragment.bindActivityViewModel(
    key: String
) = unsafeLazy { activityViewModel<T>(key) }

inline fun <reified T : ViewModel> Fragment.parentViewModel(): T =
    requireParentFragment().viewModelProvider()[T::class.java]

inline fun <reified T : ViewModel> Fragment.parentViewModel(
    factory: ViewModelProvider.Factory
) = requireParentFragment().viewModelProvider(factory)[T::class.java]

inline fun <reified T : ViewModel> Fragment.parentViewModel(
    key: String
) = requireParentFragment().viewModelProvider()[key, T::class.java]

inline fun <reified T : ViewModel> Fragment.parentViewModel(
    key: String,
    factory: ViewModelProvider.Factory
) = requireParentFragment().viewModelProvider(factory).get(key, T::class.java)

inline fun <reified T : ViewModel> Fragment.bindParentViewModel() =
    unsafeLazy { parentViewModel<T>() }

inline fun <reified T : ViewModel> Fragment.bindParentViewModel(
    crossinline factory: () -> ViewModelProvider.Factory
) = unsafeLazy { parentViewModel<T>(factory()) }

inline fun <reified T : ViewModel> Fragment.bindParentViewModel(
    key: String
) = unsafeLazy { parentViewModel<T>(key) }

inline fun <reified T : ViewModel> Fragment.targetViewModel(): T =
    requireTargetFragment().viewModelProvider()[T::class.java]

inline fun <reified T : ViewModel> Fragment.targetViewModel(
    factory: ViewModelProvider.Factory
) = requireTargetFragment().viewModelProvider(factory)[T::class.java]

inline fun <reified T : ViewModel> Fragment.targetViewModel(
    key: String
) = requireTargetFragment().viewModelProvider()[key, T::class.java]

inline fun <reified T : ViewModel> Fragment.targetViewModel(
    key: String,
    factory: ViewModelProvider.Factory
) = requireTargetFragment().viewModelProvider(factory).get(key, T::class.java)

inline fun <reified T : ViewModel> Fragment.bindTargetViewModel() =
    unsafeLazy { targetViewModel<T>() }

inline fun <reified T : ViewModel> Fragment.bindTargetViewModel(
    crossinline factory: () -> ViewModelProvider.Factory
) = unsafeLazy { targetViewModel<T>(factory()) }

inline fun <reified T : ViewModel> Fragment.bindTargetViewModel(
    key: String
) = unsafeLazy { targetViewModel<T>(key) }
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

package com.ivianuu.essentials.util.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStoreOwner

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(): T =
    ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[T::class.java]

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(
    factory: ViewModelProvider.Factory
): T = ViewModelProvider(this, factory)[T::class.java]

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(
    key: String
): T = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[key, T::class.java]

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(
    key: String,
    factory: ViewModelProvider.Factory
): T = ViewModelProvider(this, factory).get(key, T::class.java)

inline fun <reified T : ViewModel> ViewModelStoreOwner.bindViewModel(): Lazy<T> =
    unsafeLazy { getViewModel<T>() }

inline fun <reified T : ViewModel> ViewModelStoreOwner.bindViewModel(
    crossinline factory: () -> ViewModelProvider.Factory
): Lazy<T> = unsafeLazy { getViewModel<T>(factory()) }

inline fun <reified T : ViewModel> ViewModelStoreOwner.bindViewModel(
    key: String
): Lazy<T> = unsafeLazy { getViewModel<T>(key) }
/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KClass

// todo remove

val KClass<out ViewModel>.defaultViewModelKey
    get() = "androidx.lifecycle.ViewModelProvider.DefaultKey:" + java.canonicalName

fun ViewModelStoreOwner.viewModelProvider(
    factory: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory()
): ViewModelProvider = ViewModelProvider(this, factory)

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(
    factory: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory(),
    key: String = T::class.defaultViewModelKey
): T = getViewModel(T::class, factory, key)

fun <T : ViewModel> ViewModelStoreOwner.getViewModel(
    type: KClass<T>,
    factory: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory(),
    key: String = type.defaultViewModelKey
): T = viewModelProvider(factory).get(key, type.java)

inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(
    noinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    noinline factoryProvider: () -> ViewModelProvider.Factory = { ViewModelProvider.NewInstanceFactory() }
): Lazy<ViewModel> = viewModel(T::class, keyProvider, factoryProvider)

fun <T : ViewModel> ViewModelStoreOwner.viewModel(
    type: KClass<T>,
    keyProvider: () -> String = { type.defaultViewModelKey },
    factoryProvider: () -> ViewModelProvider.Factory = { ViewModelProvider.NewInstanceFactory() }
): Lazy<ViewModel> =
    lazy(LazyThreadSafetyMode.NONE) { getViewModel(type, factoryProvider(), keyProvider()) }

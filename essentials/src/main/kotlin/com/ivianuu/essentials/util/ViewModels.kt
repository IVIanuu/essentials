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
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.typeOf
import kotlin.reflect.KClass

// todo remove

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(
    from: ViewModelStoreOwner = this,
    key: String = T::class.defaultViewModelKey,
    noinline factory: () -> T = defaultViewModelFactory(T::class)
): T = getViewModel(type = T::class, from = from, key = key, factory = factory)

fun <T : ViewModel> ViewModelStoreOwner.getViewModel(
    type: KClass<T>,
    from: ViewModelStoreOwner = this,
    key: String = type.defaultViewModelKey,
    factory: () -> T = defaultViewModelFactory(type)
): T {
    val provider = ViewModelProvider(from, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = factory() as T
    })
    return provider.get(key, type.java)
}

inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(
    noinline fromProvider: () -> ViewModelStoreOwner = { this },
    noinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    noinline factory: () -> T = defaultViewModelFactory(T::class)
): Lazy<T> = viewModel(
    type = T::class,
    fromProvider = fromProvider,
    keyProvider = keyProvider,
    factory = factory
)

fun <T : ViewModel> ViewModelStoreOwner.viewModel(
    type: KClass<T>,
    fromProvider: () -> ViewModelStoreOwner = { this },
    keyProvider: () -> String = { type.defaultViewModelKey },
    factory: () -> T = defaultViewModelFactory(type)
): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) {
        getViewModel(
            type = type,
            key = keyProvider(),
            from = fromProvider(),
            factory = factory
        )
    }

inline fun <S, reified T> S.getInjectedViewModel(
    from: ViewModelStoreOwner = this,
    key: String = T::class.defaultViewModelKey
): T where T : ViewModel, S : ViewModelStoreOwner, S : InjektTrait =
    getInjectedViewModel(type = typeOf(), from = from, key = key)

fun <S, T> S.getInjectedViewModel(
    type: Type<T>,
    from: ViewModelStoreOwner = this,
    name: Any? = null,
    key: String = type.defaultViewModelKey
): T where T : ViewModel, S : ViewModelStoreOwner, S : InjektTrait =
    getViewModel(type = type.raw as KClass<T>, from = from, key = key) {
        get(type = type, name = name)
    }


inline fun <S, reified T> S.injectViewModel(
    noinline fromProvider: () -> ViewModelStoreOwner = { this },
    noinline keyProvider: () -> String = { T::class.defaultViewModelKey }
): Lazy<T> where T : ViewModel, S : ViewModelStoreOwner, S : InjektTrait = injectViewModel(
    type = typeOf(),
    fromProvider = fromProvider,
    keyProvider = keyProvider
)

fun <S, T> S.injectViewModel(
    type: Type<T>,
    fromProvider: () -> ViewModelStoreOwner = { this },
    keyProvider: () -> String = { type.defaultViewModelKey }
): Lazy<T> where T : ViewModel, S : ViewModelStoreOwner, S : InjektTrait =
    lazy(LazyThreadSafetyMode.NONE) {
        getInjectedViewModel(
            type = type,
            key = keyProvider(),
            from = fromProvider()
        )
    }

val KClass<out ViewModel>.defaultViewModelKey
    get() = "androidx.lifecycle.ViewModelProvider.DefaultKey:" + java.canonicalName
val Type<out ViewModel>.defaultViewModelKey
    get() = "androidx.lifecycle.ViewModelProvider.DefaultKey:" + raw.java.canonicalName

fun <T : ViewModel> defaultViewModelFactory(type: KClass<T>): () -> T = {
    ViewModelProvider.NewInstanceFactory().create(type.java)
}

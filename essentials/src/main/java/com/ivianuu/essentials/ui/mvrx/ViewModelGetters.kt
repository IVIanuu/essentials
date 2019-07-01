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

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.kommon.lifecycle.defaultViewModelKey
import com.ivianuu.kommon.lifecycle.doOnCreate
import com.ivianuu.kommon.lifecycle.viewModelProvider
import kotlin.reflect.KClass

inline fun <reified T : MvRxViewModel<*>> MvRxView.mvRxViewModel(
    noinline from: () -> ViewModelStoreOwner = { this },
    noinline key: () -> String = { T::class.defaultViewModelKey },
    noinline factory: () -> T
): Lazy<T> = _mvRxViewModel(T::class, from, key, factory)

@PublishedApi
internal fun <T : MvRxViewModel<*>> MvRxView._mvRxViewModel(
    type: KClass<T>,
    from: () -> ViewModelStoreOwner = { this },
    key: () -> String = { type.defaultViewModelKey },
    factory: () -> T
): Lazy<T> = unsafeLazy { _getMvRxViewModel(type, from(), key(), factory) }.also { lazy ->
    doOnCreate { lazy.value }
}

inline fun <reified T : MvRxViewModel<*>> MvRxView.getMvRxViewModel(
    from: ViewModelStoreOwner = this,
    key: String = T::class.defaultViewModelKey,
    noinline factory: () -> T
): T = _getMvRxViewModel(T::class, from, key, factory)

@PublishedApi
internal fun <T : MvRxViewModel<*>> MvRxView._getMvRxViewModel(
    type: KClass<T>,
    from: ViewModelStoreOwner = this,
    key: String = type.defaultViewModelKey,
    factory: () -> T
): T {
    return from.viewModelProvider(object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = factory() as T
    }).get(key, type.java).also { vm ->
        // invalidate this view on each state emission
        vm.liveData.observe(this, Observer { postInvalidate() })
    }
}
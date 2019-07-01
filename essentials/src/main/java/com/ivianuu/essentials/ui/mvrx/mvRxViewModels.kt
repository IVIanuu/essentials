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
import com.ivianuu.kommon.lifecycle.defaultViewModelKey
import com.ivianuu.kommon.lifecycle.viewModelProvider

inline fun <reified T : MvRxViewModel<*>> MvRxView.mvRxViewModel(
    noinline from: () -> ViewModelStoreOwner = { this },
    noinline key: () -> String = { T::class.defaultViewModelKey },
    noinline factory: () -> T
): Lazy<T> = LifecycleLazy(this) { getMvRxViewModel(from(), key(), factory) }

inline fun <reified T : MvRxViewModel<*>> MvRxView.getMvRxViewModel(
    from: ViewModelStoreOwner = this,
    key: String = T::class.defaultViewModelKey,
    noinline factory: () -> T
): T {
    return from.viewModelProvider(object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = factory() as T
    }).get(key, T::class.java).setupViewModel(this)
}

@PublishedApi
internal fun <T : MvRxViewModel<*>> T.setupViewModel(view: MvRxView): T =
    apply { liveData.observe(view, Observer { view.postInvalidate() }) }
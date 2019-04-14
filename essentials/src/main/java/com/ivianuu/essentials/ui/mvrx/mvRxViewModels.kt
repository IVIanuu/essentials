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

import com.ivianuu.essentials.ui.mvrx.lifecycle.lifecycleLazy
import com.ivianuu.essentials.ui.viewmodel.ViewModelManagerOwner
import com.ivianuu.essentials.ui.viewmodel.defaultViewModelKey
import com.ivianuu.essentials.ui.viewmodel.getViewModel

inline fun <reified T : MvRxViewModel<*>> MvRxView.mvRxViewModel(
    noinline from: () -> ViewModelManagerOwner = { this },
    noinline key: () -> String = { T::class.defaultViewModelKey },
    noinline factory: () -> T
): Lazy<T> = lifecycleLazy { getMvRxViewModel(from(), key(), factory) }

inline fun <reified T : MvRxViewModel<*>> MvRxView.getMvRxViewModel(
    from: ViewModelManagerOwner = this,
    key: String = T::class.defaultViewModelKey,
    noinline factory: () -> T
): T = getViewModel(from, key, factory).setupViewModel(this)

@PublishedApi
internal fun <T : MvRxViewModel<*>> T.setupViewModel(view: MvRxView): T =
    apply {
        subscribe(view) { view.postInvalidate() }
    }
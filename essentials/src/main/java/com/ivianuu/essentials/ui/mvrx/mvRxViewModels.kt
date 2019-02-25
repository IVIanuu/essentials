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
import com.ivianuu.essentials.ui.viewmodel.getViewModel
import com.ivianuu.kommon.lifecycle.defaultViewModelKey

inline fun <reified VM : MvRxViewModel<*>> MvRxView.viewModel(
    noinline from: () -> ViewModelManagerOwner = { this },
    noinline key: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
): Lazy<VM> = lifecycleLazy { getViewModel(from(), key(), factory) }

inline fun <reified VM : MvRxViewModel<*>> MvRxView.getViewModel(
    from: ViewModelManagerOwner = this,
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
): VM = getViewModel(from, key, factory).setupViewModel(this)

@PublishedApi
internal fun <VM : MvRxViewModel<*>> VM.setupViewModel(view: MvRxView): VM =
    apply {
        subscribe(view) { view.postInvalidate() }
    }
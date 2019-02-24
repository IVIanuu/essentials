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

package com.ivianuu.essentials.ui.mvrx.injekt

import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.getViewModel
import com.ivianuu.essentials.ui.mvrx.viewModel
import com.ivianuu.essentials.ui.viewmodel.ViewModelManagerOwner
import com.ivianuu.essentials.ui.viewmodel.defaultViewModelKey
import com.ivianuu.injekt.get

inline fun <reified VM : MvRxViewModel<*>> InjektMvRxView.viewModel(
    noinline from: () -> ViewModelManagerOwner = { this },
    noinline key: () -> String = { VM::class.defaultViewModelKey }
): Lazy<VM> = viewModel<VM>(from, key) { get() }

inline fun <reified VM : MvRxViewModel<*>> InjektMvRxView.getViewModel(
    from: ViewModelManagerOwner = this,
    key: String = VM::class.defaultViewModelKey
): VM = getViewModel(from, key) { get() }
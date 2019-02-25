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

package com.ivianuu.essentials.ui.viewmodel.injekt

import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.viewmodel.ViewModelManagerOwner
import com.ivianuu.essentials.ui.viewmodel.defaultViewModelKey
import com.ivianuu.essentials.ui.viewmodel.getViewModel
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.get
import kotlin.reflect.KClass

inline fun <T, reified VM : MvRxViewModel<*>> T.viewModel(
    type: KClass<VM>,
    crossinline from: () -> ViewModelManagerOwner = { this },
    crossinline key: () -> String = { type.defaultViewModelKey }
): Lazy<VM> where T : InjektTrait, T : ViewModelManagerOwner =
    viewModel<VM>(from, key) { get() }

inline fun <T, reified VM : MvRxViewModel<*>> T.getViewModel(
    type: KClass<VM>,
    from: ViewModelManagerOwner = this,
    key: String = type.defaultViewModelKey
): VM where T : InjektTrait, T : ViewModelManagerOwner =
    getViewModel<VM>(from, key) { get() }
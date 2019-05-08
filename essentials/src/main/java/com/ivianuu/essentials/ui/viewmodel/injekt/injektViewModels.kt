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

import com.ivianuu.essentials.ui.viewmodel.ViewModel
import com.ivianuu.essentials.ui.viewmodel.ViewModelManagerOwner
import com.ivianuu.essentials.ui.viewmodel.defaultViewModelKey
import com.ivianuu.essentials.ui.viewmodel.getViewModel
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.get

inline fun <S, reified T : ViewModel> S.viewModel(
    crossinline from: () -> ViewModelManagerOwner = { this },
    crossinline key: () -> String = { T::class.defaultViewModelKey },
    noinline parameters: ParametersDefinition? = null
): Lazy<T> where S : ViewModelManagerOwner, S : InjektTrait =
    viewModel<T>(from, key) { get(parameters = parameters) }

inline fun <S, reified T : ViewModel> S.getViewModel(
    from: ViewModelManagerOwner = this,
    key: String = T::class.defaultViewModelKey,
    noinline parameters: ParametersDefinition? = null
): T where S : ViewModelManagerOwner, S : InjektTrait =
    getViewModel(from, key) { get(parameters = parameters) }
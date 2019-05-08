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

package com.ivianuu.essentials.ui.viewmodel.director.injekt

import com.ivianuu.director.Controller
import com.ivianuu.essentials.ui.viewmodel.ViewModel
import com.ivianuu.essentials.ui.viewmodel.ViewModelManagerOwner
import com.ivianuu.essentials.ui.viewmodel.defaultViewModelKey
import com.ivianuu.essentials.ui.viewmodel.director.viewModel
import com.ivianuu.essentials.ui.viewmodel.director.viewModelManagerOwner
import com.ivianuu.essentials.ui.viewmodel.getViewModel
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.get

inline fun <S, reified T : ViewModel> S.viewModel(
    crossinline from: () -> ViewModelManagerOwner = { viewModelManagerOwner },
    crossinline key: () -> String = { T::class.defaultViewModelKey }
): Lazy<T> where S : Controller, S : InjektTrait {
    return viewModel(from, key) { get<T>() }
}

inline fun <S, reified T : ViewModel> S.getViewModel(
    from: ViewModelManagerOwner = viewModelManagerOwner,
    key: String = T::class.defaultViewModelKey
): T where S : Controller, S : InjektTrait {
    return from.getViewModel(from, key) { get<T>() }
}
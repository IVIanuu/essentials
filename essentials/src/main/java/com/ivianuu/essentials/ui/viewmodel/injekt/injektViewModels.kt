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
import com.ivianuu.injekt.get

interface InjektViewModelManagerOwner : InjektTrait, ViewModelManagerOwner

inline fun <reified T : ViewModel> InjektViewModelManagerOwner.viewModel(
    crossinline from: () -> ViewModelManagerOwner = { this },
    crossinline key: () -> String = { T::class.defaultViewModelKey }
): Lazy<T> = viewModel<T>(from, key) { get() }

inline fun <reified T : ViewModel> InjektViewModelManagerOwner.getViewModel(
    from: ViewModelManagerOwner = this,
    key: String = T::class.defaultViewModelKey
): T = getViewModel(from, key) { get() }
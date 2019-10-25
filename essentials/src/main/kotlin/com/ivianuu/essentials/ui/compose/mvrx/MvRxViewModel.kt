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

package com.ivianuu.essentials.ui.compose.mvrx

import androidx.compose.effectOf
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.ui.compose.coroutines.collect
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.viewmodel.viewModel
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.util.defaultViewModelKey
import kotlin.reflect.KClass

inline fun <reified T : MvRxViewModel<*>> mvRxViewModel(
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    factory: ViewModelProvider.Factory = +memo { ViewModelProvider.NewInstanceFactory() },
    key: String = +memo { T::class.defaultViewModelKey }
) = mvRxViewModel(T::class, from, factory, key)

fun <T : MvRxViewModel<*>> mvRxViewModel(
    type: KClass<T>,
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    factory: ViewModelProvider.Factory = +memo { ViewModelProvider.NewInstanceFactory() },
    key: String = +memo { type.defaultViewModelKey }
) = effectOf<T> {
    val viewModel = +viewModel(type, from, factory, key)
    // recompose on changes
    +collect(viewModel.flow)
    return@effectOf viewModel
}
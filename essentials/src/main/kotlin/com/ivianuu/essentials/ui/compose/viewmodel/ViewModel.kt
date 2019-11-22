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

package com.ivianuu.essentials.ui.compose.viewmodel

import androidx.compose.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.ui.compose.core.memo
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.util.defaultViewModelKey
import com.ivianuu.essentials.util.getViewModel
import kotlin.reflect.KClass

@Composable
inline fun <reified T : ViewModel> viewModel(
    from: ViewModelStoreOwner = inject(),
    factory: ViewModelProvider.Factory = memo { ViewModelProvider.NewInstanceFactory() },
    key: String = memo { T::class.defaultViewModelKey }
) = viewModel(T::class, from, factory, key)

@Composable
fun <T : ViewModel> viewModel(
    type: KClass<T>,
    from: ViewModelStoreOwner = inject(),
    factory: ViewModelProvider.Factory = memo { ViewModelProvider.NewInstanceFactory() },
    key: String = memo { type.defaultViewModelKey }
): T = memo { from.getViewModel(type, factory, key) }
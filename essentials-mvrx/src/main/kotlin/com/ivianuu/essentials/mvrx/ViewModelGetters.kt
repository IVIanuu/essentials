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

package com.ivianuu.essentials.mvrx

import androidx.compose.Composable
import com.ivianuu.essentials.ui.coroutines.collect
import com.ivianuu.essentials.ui.viewmodel.injectViewModel
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.essentials.composehelpers.sourceLocation
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.typeOf

@Composable
inline fun <T : MvRxViewModel<*>> mvRxViewModel(noinline factory: () -> T): T =
    viewModel(key = sourceLocation(), factory = factory)

@Composable
fun <T : MvRxViewModel<*>> mvRxViewModel(
    key: Any,
    factory: () -> T
): T {
    val viewModel = viewModel(key = key, factory = factory)
    // recompose on changes
    collect(viewModel.flow)
    return viewModel
}

@Composable
inline fun <reified T : MvRxViewModel<*>> injectMvRxViewModel(
    name: Any? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return injectMvRxViewModel(
        typeOf(),
        sourceLocation(),
        name,
        parameters
    )
}

@Composable
fun <T : MvRxViewModel<*>> injectMvRxViewModel(
    type: Type<T>,
    key: Any,
    name: Any? = null,
    parameters: ParametersDefinition? = null
): T {
    val viewModel = injectViewModel(type, key, name, parameters)
    // recompose on changes
    collect(viewModel.flow)
    return viewModel
}
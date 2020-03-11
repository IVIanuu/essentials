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
import com.ivianuu.essentials.android.ui.core.pointInComposition
import com.ivianuu.essentials.android.ui.coroutines.collect
import com.ivianuu.essentials.android.ui.viewmodel.injectViewModel
import com.ivianuu.essentials.android.ui.viewmodel.viewModel
import com.ivianuu.injekt.Key
import com.ivianuu.injekt.Parameters
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.emptyParameters
import com.ivianuu.injekt.keyOf


@Composable
inline fun <T : MvRxViewModel<*>> mvRxViewModel(noinline factory: () -> T): T =
    viewModel(key = pointInComposition(), factory = factory)

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
    qualifier: Qualifier = Qualifier.None,
    parameters: Parameters = emptyParameters()
): T = injectMvRxViewModel(
    key = keyOf(qualifier = qualifier),
    viewModelKey = pointInComposition(),
    parameters = parameters
)

@Composable
fun <T : MvRxViewModel<*>> injectMvRxViewModel(
    key: Key<T>,
    viewModelKey: Any,
    parameters: Parameters = emptyParameters()
): T {
    val viewModel = injectViewModel(key = key, viewModelKey = viewModelKey, parameters = parameters)
    // recompose on changes
    collect(viewModel.flow)
    return viewModel
}

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

package com.ivianuu.essentials.ui.viewmodel

import androidx.compose.Composable
import com.ivianuu.essentials.ui.base.ViewModel
import com.ivianuu.essentials.composehelpers.current
import com.ivianuu.essentials.ui.core.retain
import com.ivianuu.essentials.ui.injekt.ComponentAmbient
import com.ivianuu.essentials.composehelpers.sourceLocation
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.typeOf

@Composable
inline fun <T : ViewModel> viewModel(noinline factory: () -> T): T =
    viewModel(key = sourceLocation(), factory = factory)

@Composable
fun <T : ViewModel> viewModel(
    key: Any,
    factory: () -> T
): T = retain(key = key, init = factory)

@Composable
inline fun <reified T : ViewModel> injectViewModel(
    name: Any? = null,
    noinline parameters: ParametersDefinition? = null
): T = injectViewModel(
    typeOf(),
    sourceLocation(),
    name,
    parameters
)

@Composable
fun <T : ViewModel> injectViewModel(
    type: Type<T>,
    key: Any,
    name: Any? = null,
    parameters: ParametersDefinition? = null
): T {
    val component = ComponentAmbient.current
    return viewModel(
        key = key,
        factory = { component.get(type = type, name = name, parameters = parameters) }
    )
}

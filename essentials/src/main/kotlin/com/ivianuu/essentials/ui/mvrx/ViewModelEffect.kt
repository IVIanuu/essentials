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

package com.ivianuu.essentials.ui.mvrx

import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ambient
import com.ivianuu.compose.common.flow
import com.ivianuu.essentials.ui.compose.injekt.ComponentAmbient
import com.ivianuu.essentials.ui.compose.viewmodel.viewModel
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.get
import kotlin.reflect.KClass

inline fun <reified VM : MvRxViewModel<S>, reified S> ComponentComposition.mvRxViewModel(
    type: KClass<VM>,
    noinline factory: () -> VM
): Pair<S, VM> {
    val viewModel = viewModel(type, factory)
    val state = flow(viewModel.state, viewModel.flow)
    return state to viewModel
}

inline fun <reified VM : MvRxViewModel<S>, reified S> ComponentComposition.injectMvRxViewModel(
    type: KClass<VM>,
    name: Any? = null,
    noinline parameters: ParametersDefinition? = null
): Pair<S, VM> {
    val component = ambient(ComponentAmbient)
    return mvRxViewModel(type) { component.get(name, parameters) }
}
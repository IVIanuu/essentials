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

package com.ivianuu.essentials.ui.mvrx.list


import com.ivianuu.director.Controller

import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.withState
import com.ivianuu.list.ModelController
import com.ivianuu.list.common.modelController

fun <A : MvRxViewModel<B>, B> Any.mvRxModelController(
    viewModel1: A,
    buildModels: ModelController.(state: B) -> Unit
): ModelController = modelController {
    if (this@mvRxModelController is Controller && view == null
    ) return@modelController
    withState(viewModel1) { buildModels.invoke(this, it) }
}

fun <A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D> Any.simpleModelController(
    viewModel1: A,
    viewModel2: C,
    buildModels: ModelController.(state1: B, state2: D) -> Unit
): ModelController = modelController {
    if (this@simpleModelController is Controller && view == null
    ) return@modelController
    withState(
        viewModel1,
        viewModel2
    ) { state1, state2 -> buildModels.invoke(this, state1, state2) }
}

fun <A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D, E : MvRxViewModel<F>, F> Any.simpleModelController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    buildModels: ModelController.(state1: B, state2: D, state3: F) -> Unit
): ModelController = modelController {
    if (this@simpleModelController is Controller && view == null
    ) return@modelController
    withState(
        viewModel1,
        viewModel2,
        viewModel3
    ) { state1, state2, state3 ->
        buildModels.invoke(this, state1, state2, state3)
    }
}

fun <A : MvRxViewModel<B>,
        B,
        C : MvRxViewModel<D>, D, E : MvRxViewModel<F>, F, G : MvRxViewModel<H>, H> Any.simpleModelController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    buildModels: ModelController.(state1: B, state2: D, state3: F, state4: H) -> Unit
): ModelController = modelController {
    if (this@simpleModelController is Controller && view == null
    ) return@modelController
    withState(
        viewModel1,
        viewModel2,
        viewModel3,
        viewModel4
    ) { state1, state2, state3, state4 ->
        buildModels.invoke(this, state1, state2, state3, state4)
    }
}

fun <A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D, E : MvRxViewModel<F>, F, G : MvRxViewModel<H>, H, I : MvRxViewModel<J>, J> Any.simpleModelController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    viewModel5: I,
    buildModels: ModelController.(state1: B, state2: D, state3: F, state4: H, state5: J) -> Unit
): ModelController = modelController {
    if (this@simpleModelController is Controller && view == null) return@modelController
    withState(
        viewModel1,
        viewModel2,
        viewModel3,
        viewModel4,
        viewModel5
    ) { state1, state2, state3, state4, state5 ->
        buildModels.invoke(this, state1, state2, state3, state4, state5)
    }
}
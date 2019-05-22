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

package com.ivianuu.essentials.ui.mvrx.epoxy

import com.airbnb.epoxy.EpoxyController
import com.ivianuu.director.Controller
import com.ivianuu.essentials.ui.epoxy.epoxyController

import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.withState

fun <A : MvRxViewModel<B>, B> Any.mvRxEpoxyController(
    viewModel1: A,
    buildItems: EpoxyController.(state: B) -> Unit
): EpoxyController = epoxyController {
    if (this@mvRxEpoxyController is Controller && view == null
    ) return@epoxyController
    withState(viewModel1) { buildItems.invoke(this, it) }
}

fun <A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D> Any.mvRxEpoxyController(
    viewModel1: A,
    viewModel2: C,
    buildItems: EpoxyController.(state1: B, state2: D) -> Unit
): EpoxyController = epoxyController {
    if (this@mvRxEpoxyController is Controller && view == null
    ) return@epoxyController
    withState(
        viewModel1,
        viewModel2
    ) { state1, state2 -> buildItems.invoke(this, state1, state2) }
}

fun <A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D, E : MvRxViewModel<F>, F> Any.mvRxEpoxyController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    buildItems: EpoxyController.(state1: B, state2: D, state3: F) -> Unit
): EpoxyController = epoxyController {
    if (this@mvRxEpoxyController is Controller && view == null
    ) return@epoxyController
    withState(
        viewModel1,
        viewModel2,
        viewModel3
    ) { state1, state2, state3 ->
        buildItems.invoke(this, state1, state2, state3)
    }
}

fun <A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D, E : MvRxViewModel<F>, F, G : MvRxViewModel<H>, H> Any.mvRxEpoxyController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    buildItems: EpoxyController.(state1: B, state2: D, state3: F, state4: H) -> Unit
): EpoxyController = epoxyController {
    if (this@mvRxEpoxyController is Controller && view == null
    ) return@epoxyController
    withState(
        viewModel1,
        viewModel2,
        viewModel3,
        viewModel4
    ) { state1, state2, state3, state4 ->
        buildItems.invoke(this, state1, state2, state3, state4)
    }
}

fun <A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D, E : MvRxViewModel<F>, F, G : MvRxViewModel<H>, H, I : MvRxViewModel<J>, J> Any.mvRxEpoxyController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    viewModel5: I,
    buildItems: EpoxyController.(state1: B, state2: D, state3: F, state4: H, state5: J) -> Unit
): EpoxyController = epoxyController {
    if (this@mvRxEpoxyController is Controller && view == null) return@epoxyController
    withState(
        viewModel1,
        viewModel2,
        viewModel3,
        viewModel4,
        viewModel5
    ) { state1, state2, state3, state4, state5 ->
        buildItems.invoke(this, state1, state2, state3, state4, state5)
    }
}
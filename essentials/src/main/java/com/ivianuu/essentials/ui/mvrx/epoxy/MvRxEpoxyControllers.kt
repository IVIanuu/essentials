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

package com.ivianuu.essentials.ui.mvrx.epoxy

import com.airbnb.epoxy.EpoxyController
import com.ivianuu.director.Controller
import com.ivianuu.essentials.ui.epoxy.epoxyController
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel

fun <T : MvRxView, A : MvRxViewModel<B>, B> T.mvRxEpoxyController(
    viewModel1: A,
    buildItems: EpoxyController.(state: B) -> Unit
): EpoxyController = epoxyController {
    if (this@mvRxEpoxyController !is Controller || view != null) {
        buildItems.invoke(this, viewModel1.state)
    }
}

fun <T : MvRxView, A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D> T.mvRxEpoxyController(
    viewModel1: A,
    viewModel2: C,
    buildItems: EpoxyController.(state1: B, state2: D) -> Unit
): EpoxyController = epoxyController {
    if (this@mvRxEpoxyController !is Controller || view != null) {
        buildItems.invoke(
            this,
            viewModel1.state,
            viewModel2.state
        )
    }
}

fun <T : MvRxView, A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D, E : MvRxViewModel<F>, F> T.mvRxEpoxyController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    buildItems: EpoxyController.(state1: B, state2: D, state3: F) -> Unit
): EpoxyController = epoxyController {
    if (this@mvRxEpoxyController !is Controller || view != null) {
        buildItems.invoke(
            this,
            viewModel1.state,
            viewModel2.state,
            viewModel3.state
        )
    }
}

fun <T : MvRxView, A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D, E : MvRxViewModel<F>, F, G : MvRxViewModel<H>, H> T.mvRxEpoxyController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    buildItems: EpoxyController.(state1: B, state2: D, state3: F, state4: H) -> Unit
): EpoxyController = epoxyController {
    if (this@mvRxEpoxyController !is Controller || view != null) {
        buildItems.invoke(
            this,
            viewModel1.state,
            viewModel2.state,
            viewModel3.state,
            viewModel4.state
        )
    }
}

fun <T : MvRxView, A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D, E : MvRxViewModel<F>, F, G : MvRxViewModel<H>, H, I : MvRxViewModel<J>, J> T.mvRxEpoxyController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    viewModel5: I,
    buildItems: EpoxyController.(state1: B, state2: D, state3: F, state4: H, state5: J) -> Unit
): EpoxyController = epoxyController {
    if (this@mvRxEpoxyController !is Controller || view != null) {
        buildItems.invoke(
            this,
            viewModel1.state,
            viewModel2.state,
            viewModel3.state,
            viewModel4.state,
            viewModel5.state
        )
    }
}
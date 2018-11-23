package com.ivianuu.essentials.ui.mvrx

import com.airbnb.epoxy.EpoxyController
import com.ivianuu.director.Controller
import com.ivianuu.epoxyktx.epoxyController

fun <A : MvRxViewModel<B>, B : MvRxState> Any.simpleEpoxyController(
    viewModel1: A,
    buildModels: EpoxyController.(state: B) -> Unit
) = epoxyController {
    if (this@simpleEpoxyController is Controller && view == null
    ) return@epoxyController
    withState(viewModel1) { buildModels.invoke(this, it) }
}

fun <A : MvRxViewModel<B>, B : MvRxState, C : MvRxViewModel<D>, D : MvRxState> Any.simpleEpoxyController(
    viewModel1: A,
    viewModel2: C,
    buildModels: EpoxyController.(state1: B, state2: D) -> Unit
) = epoxyController {
    if (this@simpleEpoxyController is Controller && view == null
    ) return@epoxyController
    withState(viewModel1, viewModel2) { state1, state2 -> buildModels.invoke(this, state1, state2) }
}

fun <A : MvRxViewModel<B>, B : MvRxState, C : MvRxViewModel<D>, D : MvRxState, E : MvRxViewModel<F>, F : MvRxState> Any.simpleEpoxyController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    buildModels: EpoxyController.(state1: B, state2: D, state3: F) -> Unit
) = epoxyController {
    if (this@simpleEpoxyController is Controller && view == null
    ) return@epoxyController
    withState(viewModel1, viewModel2, viewModel3) { state1, state2, state3 ->
        buildModels.invoke(this, state1, state2, state3)
    }
}

fun <A : MvRxViewModel<B>,
        B : MvRxState,
        C : MvRxViewModel<D>, D : MvRxState, E : MvRxViewModel<F>, F : MvRxState, G : MvRxViewModel<H>, H : MvRxState> Any.simpleEpoxyController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    buildModels: EpoxyController.(state1: B, state2: D, state3: F, state4: H) -> Unit
) = epoxyController {
    if (this@simpleEpoxyController is Controller && view == null
    ) return@epoxyController
    withState(viewModel1, viewModel2, viewModel3, viewModel4) { state1, state2, state3, state4 ->
        buildModels.invoke(this, state1, state2, state3, state4)
    }
}

fun <A : MvRxViewModel<B>, B : MvRxState, C : MvRxViewModel<D>, D : MvRxState, E : MvRxViewModel<F>, F : MvRxState, G : MvRxViewModel<H>, H : MvRxState, I : MvRxViewModel<J>, J : MvRxState> Any.simpleEpoxyController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    viewModel5: I,
    buildModels: EpoxyController.(state1: B, state2: D, state3: F, state4: H, state5: J) -> Unit
) = epoxyController {
    if (this@simpleEpoxyController is Controller && view == null
    ) return@epoxyController
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
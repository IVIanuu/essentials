package com.ivianuu.essentials.ui.state

import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.util.ext.epoxyController

fun <A : StateViewModel<B>, B : Any> BaseFragment.stateEpoxyController(
    viewModel1: A,
    buildModels: EpoxyController.(state: B) -> Unit
) = epoxyController {
    if (view == null) return@epoxyController
    withState(viewModel1) { buildModels.invoke(this, it) }
}

fun <A : StateViewModel<B>, B : Any, C : StateViewModel<D>, D : Any> BaseFragment.stateEpoxyController(
    viewModel1: A,
    viewModel2: C,
    buildModels: EpoxyController.(state1: B, state2: D) -> Unit
) = epoxyController {
    if (view == null) return@epoxyController
    withState(viewModel1, viewModel2) { state1, state2 -> buildModels.invoke(this, state1, state2) }
}

fun <A : StateViewModel<B>, B : Any, C : StateViewModel<D>, D : Any, E : StateViewModel<F>, F : Any> BaseFragment.stateEpoxyController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    buildModels: EpoxyController.(state1: B, state2: D, state3: F) -> Unit
) = epoxyController {
    if (view == null) return@epoxyController
    withState(viewModel1, viewModel2, viewModel3) { state1, state2, state3 ->
        buildModels.invoke(this, state1, state2, state3)
    }
}

fun <A : StateViewModel<B>,
        B : Any,
        C : StateViewModel<D>, D : Any, E : StateViewModel<F>, F : Any, G : StateViewModel<H>, H : Any> BaseFragment.stateEpoxyController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    buildModels: EpoxyController.(state1: B, state2: D, state3: F, state4: H) -> Unit
) = epoxyController {
    if (view == null) return@epoxyController
    withState(viewModel1, viewModel2, viewModel3, viewModel4) { state1, state2, state3, state4 ->
        buildModels.invoke(this, state1, state2, state3, state4)
    }
}

fun <A : StateViewModel<B>, B : Any, C : StateViewModel<D>, D : Any, E : StateViewModel<F>, F : Any, G : StateViewModel<H>, H : Any, I : StateViewModel<J>, J : Any> BaseFragment.stateEpoxyController(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    viewModel5: I,
    buildModels: EpoxyController.(state1: B, state2: D, state3: F, state4: H, state5: J) -> Unit
) = epoxyController {
    if (view == null) return@epoxyController
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
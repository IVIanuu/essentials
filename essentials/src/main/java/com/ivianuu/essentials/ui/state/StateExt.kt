package com.ivianuu.essentials.ui.state

fun <A : StateViewModel<B>, B : Any, C> withState(viewModel1: A, block: (state: B) -> C) =
    block(viewModel1.state)

fun <A : StateViewModel<B>, B : Any, C : StateViewModel<D>, D : Any, E> withState(
    viewModel1: A,
    viewModel2: C,
    block: (state1: B, state2: D) -> E
) = block(viewModel1.state, viewModel2.state)

fun <A : StateViewModel<B>, B : Any, C : StateViewModel<D>, D : Any, E : StateViewModel<F>, F : Any, G> withState(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    block: (state1: B, state2: D, state3: F) -> G
) = block(viewModel1.state, viewModel2.state, viewModel3.state)

fun <A : StateViewModel<B>, B : Any, C : StateViewModel<D>, D : Any, E : StateViewModel<F>, F : Any, G : StateViewModel<H>, H : Any, I> withState(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    block: (state1: B, state2: D, state3: F, state4: H) -> I
) = block(viewModel1.state, viewModel2.state, viewModel3.state, viewModel4.state)

fun <A : StateViewModel<B>, B : Any, C : StateViewModel<D>, D : Any, E : StateViewModel<F>, F : Any, G : StateViewModel<H>, H : Any, I : StateViewModel<J>, J : Any, K> withState(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    viewModel5: I,
    block: (state1: B, state2: D, state3: F, state4: H, state5: J) -> K
) = block(viewModel1.state, viewModel2.state, viewModel3.state, viewModel4.state, viewModel5.state)
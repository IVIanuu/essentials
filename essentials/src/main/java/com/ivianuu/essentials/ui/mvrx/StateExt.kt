package com.ivianuu.essentials.ui.mvrx

fun <A : MvRxViewModel<B>, B : MvRxState, C> withState(viewModel1: A, block: (state: B) -> C): C =
    block(viewModel1.state)

fun <A : MvRxViewModel<B>, B : MvRxState, C : MvRxViewModel<D>, D : MvRxState, E> withState(
    viewModel1: A,
    viewModel2: C,
    block: (state1: B, state2: D) -> E
): E = block(viewModel1.state, viewModel2.state)

fun <A : MvRxViewModel<B>, B : MvRxState, C : MvRxViewModel<D>, D : MvRxState, E : MvRxViewModel<F>, F : MvRxState, G> withState(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    block: (state1: B, state2: D, state3: F) -> G
): G = block(viewModel1.state, viewModel2.state, viewModel3.state)

fun <A : MvRxViewModel<B>, B : MvRxState, C : MvRxViewModel<D>, D : MvRxState, E : MvRxViewModel<F>, F : MvRxState, G : MvRxViewModel<H>, H : MvRxState, I> withState(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    block: (state1: B, state2: D, state3: F, state4: H) -> I
): I = block(viewModel1.state, viewModel2.state, viewModel3.state, viewModel4.state)

fun <A : MvRxViewModel<B>, B : MvRxState, C : MvRxViewModel<D>, D : MvRxState, E : MvRxViewModel<F>, F : MvRxState, G : MvRxViewModel<H>, H : MvRxState, I : MvRxViewModel<J>, J : MvRxState, K> withState(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    viewModel5: I,
    block: (state1: B, state2: D, state3: F, state4: H, state5: J) -> K
): K = block(viewModel1.state, viewModel2.state, viewModel3.state, viewModel4.state, viewModel5.state)
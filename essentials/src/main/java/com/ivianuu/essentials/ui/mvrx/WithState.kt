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

package com.ivianuu.essentials.ui.mvrx

inline fun <A : MvRxViewModel<B>, B, C> withState(viewModel1: A, block: (state: B) -> C): C =
    block(viewModel1.currentState)

inline fun <A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D, E> withState(
    viewModel1: A,
    viewModel2: C,
    block: (state1: B, state2: D) -> E
): E = block(viewModel1.currentState, viewModel2.currentState)

inline fun <A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D, E : MvRxViewModel<F>, F, G> withState(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    block: (state1: B, state2: D, state3: F) -> G
): G = block(viewModel1.currentState, viewModel2.currentState, viewModel3.currentState)

inline fun <A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D, E : MvRxViewModel<F>, F, G : MvRxViewModel<H>, H, I> withState(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    block: (state1: B, state2: D, state3: F, state4: H) -> I
): I = block(
    viewModel1.currentState,
    viewModel2.currentState,
    viewModel3.currentState,
    viewModel4.currentState
)

inline fun <A : MvRxViewModel<B>, B, C : MvRxViewModel<D>, D, E : MvRxViewModel<F>, F, G : MvRxViewModel<H>, H, I : MvRxViewModel<J>, J, K> withState(
    viewModel1: A,
    viewModel2: C,
    viewModel3: E,
    viewModel4: G,
    viewModel5: I,
    block: (state1: B, state2: D, state3: F, state4: H, state5: J) -> K
): K = block(
    viewModel1.currentState,
    viewModel2.currentState,
    viewModel3.currentState,
    viewModel4.currentState,
    viewModel5.currentState
)
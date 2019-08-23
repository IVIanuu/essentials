package com.ivianuu.essentials.ui.mvrx

import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.common.coroutineScope
import com.ivianuu.compose.memo
import com.ivianuu.compose.state
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <S, A> ComponentComposition.stateStore(
    initialState: S,
    reducer: suspend (S, A) -> S
): Pair<S, (A) -> Unit> {
    val state = state { initialState }
    val coroutineScope = coroutineScope
    val dispatch = memo<(A) -> Unit> {
        { action ->
            coroutineScope.launch {
                withContext(Dispatchers.Default) {
                    state.value = reducer(state.value, action)
                }
            }
        }
    }

    return state.value to dispatch
}
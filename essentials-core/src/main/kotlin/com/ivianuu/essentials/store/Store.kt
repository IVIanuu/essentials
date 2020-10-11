package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.awaitCancellation
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface Store<S, A> {
    val state: StateFlow<S>
    fun dispatch(action: A)
}

interface StoreScope<S, A> : CoroutineScope {
    val state: MutableStateFlow<S>
    val actions: Flow<A>
}

suspend fun <S, A> StoreScope<S, A>.onEachAction(block: suspend (A) -> Unit) {
    actions.collect(block)
}

fun <S, A> StoreScope<S, A>.setState(reducer: suspend S.() -> S) {
    launch {
        val newState = reducer(state.value)
        state.value = newState
    }
}

var <S, A> StoreScope<S, A>.currentState: S
    get() = state.value
    set(value) {
        state.value = value
    }

@BuilderInference
fun <S, A> CoroutineScope.store(
    initial: S,
    block: suspend StoreScope<S, A>.() -> Unit,
): Store<S, A> = StoreImpl(this, initial, block)

@BuilderInference
fun <S, A> storeProvider(
    initial: S,
    block: suspend StoreScope<S, A>.() -> Unit,
): (CoroutineScope) -> Store<S, A> = {
    it.store(initial, block)
}

@FunBinding
fun @Assisted StoreScope<*, *>.enableLogging(logger: Logger) {
    logger.d("initialize with state $currentState")

    state
        .drop(1)
        .onEach { logger.d("new state -> $it") }
        .launchIn(this)

    actions
        .onEach { logger.d("on action -> $it") }
        .launchIn(this)

    launch {
        runWithCleanup(
            block = { awaitCancellation() },
            cleanup = { logger.d("cancel") }
        )
    }
}

internal class StoreImpl<S, A>(
    private val scope: CoroutineScope,
    initial: S,
    block: suspend StoreScope<S, A>.() -> Unit,
) : Store<S, A>, StoreScope<S, A>, CoroutineScope by scope {

    override val state = MutableStateFlow(initial)
    override val actions = EventFlow<A>()

    init {
        launch {
            block()
        }
    }

    override fun dispatch(action: A) {
        actions.offer(action)
    }

}

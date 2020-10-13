package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.store.StoreImpl.StoreMessage.DispatchAction
import com.ivianuu.essentials.store.StoreImpl.StoreMessage.SetState
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface Store<S, A> {
    val state: StateFlow<S>
    fun dispatch(action: A)
}

interface StoreScope<S, A> : CoroutineScope {
    val actions: Flow<A>
    val state: Flow<S>
    suspend fun setState(block: suspend S.() -> S)
}

suspend fun <S, A> StoreScope<S, A>.onEachAction(block: suspend (A) -> Unit) {
    actions.collect(block)
}

suspend fun <S, A> StoreScope<S, A>.currentState(): S = state.first()

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
suspend fun @Assisted StoreScope<*, *>.enableLogging(logger: Logger) {
    logger.d("initialize with state ${currentState()}")

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

fun <S, A, T> Flow<T>.setStateIn(
    scope: StoreScope<S, A>,
    reducer: suspend S.(T) -> S,
): Job {
    return onEach {
        scope.setState {
            reducer(it)
        }
    }
        .launchIn(scope)
}

internal class StoreImpl<S, A>(
    private val scope: CoroutineScope,
    initial: S,
    block: suspend StoreScope<S, A>.() -> Unit,
) : Store<S, A>, StoreScope<S, A>, CoroutineScope by scope {

    override val state = MutableStateFlow(initial)
    override val actions = EventFlow<A>()

    private val actor = actor<StoreMessage<S, A>>(capacity = DEFAULT_BUFFER_SIZE) {
        for (msg in channel) {
            when (msg) {
                is DispatchAction -> actions.send(msg.action)
                is SetState -> {
                    val currentState = state.value
                    val newState = msg.block(currentState)
                    state.value = newState
                    msg.acknowledged.complete(Unit)
                }
            }.exhaustive
        }
    }

    init {
        launch { block() }
    }

    override fun dispatch(action: A) {
        actor.offer(DispatchAction(action))
    }

    override suspend fun setState(block: suspend S.() -> S) {
        val acknowledged = CompletableDeferred<Unit>()
        actor.offer(SetState(acknowledged, block))
        acknowledged.await()
    }

    sealed class StoreMessage<S, A> {
        data class DispatchAction<S, A>(val action: A) : StoreMessage<S, A>()
        data class SetState<S, A>(
            val acknowledged: CompletableDeferred<Unit>,
            val block: suspend S.() -> S,
        ) : StoreMessage<S, A>()
    }
}

package com.ivianuu.essentials.ui.store

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.remember
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.awaitCancellation
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.flowAsResource
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface Store<S, A> {
    val state: StateFlow<S>
    fun dispatch(action: A)
}

interface StoreScope<S, A> {
    val state: MutableStateFlow<S>
    val actions: Flow<A>
    val scope: CoroutineScope
}

@Reader
inline fun <S, A> StoreScope<S, A>.enableLogging() {
    d { "initialize with state $currentState" }

    state
        .drop(1)
        .onEach { d { "new state -> $it" } }
        .launchIn(scope)

    actions
        .onEach { d { "on action -> $it" } }
        .launchIn(scope)

    scope.launch {
        runWithCleanup(
            block = { awaitCancellation() },
            cleanup = { d { "cancel" } }
        )
    }
}

fun <S, V> StoreScope<S, *>.execute(
    block: suspend () -> V,
    reducer: suspend S.(Resource<V>) -> S
): Job {
    return flow { emit(block()) }
        .executeIn(this, reducer)
}

fun <S, V> Flow<V>.executeIn(
    storeScope: StoreScope<S, *>,
    reducer: suspend S.(Resource<V>) -> S
): Job {
    return flowAsResource()
        .onEach {
            storeScope.setState {
                reducer(it)
            }
        }
        .launchIn(storeScope.scope)
}


fun <A> StoreScope<*, A>.onEachAction(block: suspend (A) -> Unit) {
    actions
        .onEach(block)
        .launchIn(scope)
}

fun <S> StoreScope<S, *>.setState(reducer: suspend S.() -> S) {
    scope.launch {
        val newState = reducer(state.value)
        state.value = newState
    }
}

var <S> StoreScope<S, *>.currentState: S
    get() = state.value
    set(value) {
        state.value = value
    }

@BuilderInference
fun <S, A> CoroutineScope.baseStore(
    initial: S,
    block: suspend StoreScope<S, A>.() -> Unit
): Store<S, A> = StoreImpl(this, initial, block)

@Reader
@BuilderInference
inline fun <S, A> CoroutineScope.store(
    initial: S,
    noinline block: suspend StoreScope<S, A>.() -> Unit
): Store<S, A> = baseStore(initial) {
    enableLogging()
    block()
}

internal class StoreImpl<S, A>(
    override val scope: CoroutineScope,
    initial: S,
    block: suspend StoreScope<S, A>.() -> Unit
) : Store<S, A>, StoreScope<S, A> {

    override val state = MutableStateFlow(initial)
    override val actions = EventFlow<A>()

    init {
        scope.launch {
            block()
        }
    }

    override fun dispatch(action: A) {
        actions.offer(action)
    }

}

@Composable
operator fun <S> Store<S, *>.component1() = observedState

@Composable
operator fun <A> Store<*, A>.component2(): (A) -> Unit = { dispatch(it) }

@Composable
val <S> Store<S, *>.observedState: S
    get() = state.collectAsState().value

// todo remove overload once compiler is fixed
@Reader
@Composable
fun <S, A> rememberStore(
    init: CoroutineScope.() -> Store<S, A>
): Store<S, A> = rememberStore(inputs = *emptyArray(), init = init)

@Reader
@Composable
fun <S, A> rememberStore(
    vararg inputs: Any?,
    init: CoroutineScope.() -> Store<S, A>
): Store<S, A> {
    val scope = rememberCoroutineScope { dispatchers.default }
    return remember(*inputs) { init(scope) }
}

@Composable
private fun rememberCoroutineScope(
    getContext: () -> CoroutineContext = { EmptyCoroutineContext }
): CoroutineScope {
    return androidx.compose.rememberCoroutineScope(getContext)
}

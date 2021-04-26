package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.tuples.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.jvm.*

fun <S> CoroutineScope.state(
    initial: S,
    block: suspend StateScope<S>.() -> Unit
): StateFlow<S> {
    val state = MutableStateFlow(initial)
    val stateScope = object : StateScope<S>, CoroutineScope by this {
        override val state: Flow<S>
            get() = state
        override suspend fun update(transform: S.() -> S): S = state.update(transform)
    }
    stateScope.launch(start = CoroutineStart.UNDISPATCHED) { stateScope.block() }
    return state
}

interface StateScope<S> : CoroutineScope {
    val state: Flow<S>
    suspend fun update(transform: S.() -> S): S
    fun Flow<S.() -> S>.update(): Job = launch { collect { update(it) } }
    fun <T> Flow<T>.update(transform: S.(T) -> S): Job =
        map<T, S.() -> S> { { transform(it) } }.update()
}

suspend fun <S> StateScope<S>.action(
    lens: Lens<S, () -> Unit>,
    block: suspend () -> Unit
) {
    val callback: () -> Unit = { launch { block() } }
    update { lens.set(this, callback) }
}

suspend fun <S, P1> StateScope<S>.action(
    lens: Lens<S, (P1) -> Unit>,
    block: suspend (P1) -> Unit
) {
    val callback: (P1) -> Unit = { p1 -> launch { block(p1) } }
    update { lens.set(this, callback) }
}

suspend fun <S, P1, P2> StateScope<S>.action(
    lens: Lens<S, (P1, P2) -> Unit>,
    block: suspend (P1, P2) -> Unit
) {
    val callback: (P1, P2) -> Unit = { p1, p2 -> launch { block(p1, p2) } }
    update { lens.set(this, callback) }
}

suspend fun <S, P1, P2, P3> StateScope<S>.action(
    lens: Lens<S, (P1, P2, P3) -> Unit>,
    block: suspend (P1, P2, P3) -> Unit
) {
    val callback: (P1, P2, P3) -> Unit = { p1, p2, p3 -> launch { block(p1, p2, p3) } }
    update { lens.set(this, callback) }
}

suspend fun <S, P1, P2, P3, P4> StateScope<S>.action(
    lens: Lens<S, (P1, P2, P3, P4) -> Unit>,
    block: suspend (P1, P2, P3, P4) -> Unit
) {
    val callback: (P1, P2, P3, P4) -> Unit = { p1, p2, p3, p4 -> launch { block(p1, p2, p3, p4) } }
    update { lens.set(this, callback) }
}

suspend fun <S, P1, P2, P3, P4, P5> StateScope<S>.action(
    lens: Lens<S, (P1, P2, P3, P4, P5) -> Unit>,
    block: suspend (P1, P2, P3, P4, P5) -> Unit
) {
    val callback: (P1, P2, P3, P4, P5) -> Unit = { p1, p2, p3, p4, p5 -> launch { block(p1, p2, p3, p4, p5) } }
    update { lens.set(this, callback) }
}

@JvmName("actions0")
suspend fun <S> StateScope<S>.actions(lens: Lens<S, () -> Unit>): Flow<Unit> {
    val events = EventFlow<Unit>()
    action(lens) { events.emit(Unit) }
    return events
}

@JvmName("actions1")
suspend fun <S, P1> StateScope<S>.actions(lens: Lens<S, (P1) -> Unit>): Flow<P1> {
    val events = EventFlow<P1>()
    action(lens) { p1 -> events.emit(p1) }
    return events
}

@JvmName("actions2")
suspend fun <S, P1, P2> StateScope<S>.actions(lens: Lens<S, (P1, P2) -> Unit>): Flow<Tuple2<P1, P2>> {
    val events = EventFlow<Tuple2<P1, P2>>()
    action(lens) { p1, p2 -> events.emit(tupleOf(p1, p2)) }
    return events
}

@JvmName("actions3")
suspend fun <S, P1, P2, P3> StateScope<S>.actions(lens: Lens<S, (P1, P2, P3) -> Unit>): Flow<Tuple3<P1, P2, P3>> {
    val events = EventFlow<Tuple3<P1, P2, P3>>()
    action(lens) { p1, p2, p3 -> events.emit(tupleOf(p1, p2, p3)) }
    return events
}

@JvmName("actions4")
suspend fun <S, P1, P2, P3, P4> StateScope<S>.actions(lens: Lens<S, (P1, P2, P3, P4) -> Unit>): Flow<Tuple4<P1, P2, P3, P4>> {
    val events = EventFlow<Tuple4<P1, P2, P3, P4>>()
    action(lens) { p1, p2, p3, p4 -> events.emit(tupleOf(p1, p2, p3, p4)) }
    return events
}

@JvmName("actions5")
suspend fun <S, P1, P2, P3, P4, P5> StateScope<S>.actions(lens: Lens<S, (P1, P2, P3, P4, P5) -> Unit>): Flow<Tuple5<P1, P2, P3, P4, P5>> {
    val events = EventFlow<Tuple5<P1, P2, P3, P4, P5>>()
    action(lens) { p1, p2, p3, p4, p5 -> events.emit(tupleOf(p1, p2, p3, p4, p5)) }
    return events
}

/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.scope
import com.ivianuu.essentials.coroutines.update2
import com.ivianuu.essentials.optics.Lens
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.resourceFlow
import com.ivianuu.essentials.tuples.Tuple2
import com.ivianuu.essentials.tuples.Tuple3
import com.ivianuu.essentials.tuples.Tuple4
import com.ivianuu.essentials.tuples.Tuple5
import com.ivianuu.essentials.tuples.tupleOf
import com.ivianuu.injekt.Inject1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmName

@Inject1<CoroutineScope> fun <S> state(
  initial: S,
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend StateScope<S>.() -> Unit
): StateFlow<S> {
  val state = MutableStateFlow(initial)

  val stateScope = object : StateScope<S>, CoroutineScope by scope {
    override val state: Flow<S>
      get() = state

    override suspend fun update(transform: S.() -> S): S = state.update2(transform)
  }

  stateScope.launch(scope.coroutineContext + context, CoroutineStart.UNDISPATCHED) {
    stateScope.block()
  }

  return state
}

interface StateScope<S> : CoroutineScope {
  val state: Flow<S>

  suspend fun update(transform: S.() -> S): S

  fun Flow<S.() -> S>.update(): Job = launch { collect { update(it) } }

  fun <T> Flow<T>.update(transform: S.(T) -> S): Job =
    map<T, S.() -> S> { { transform(it) } }.update()
}

fun <S, T> StateScope<S>.produceResource(
  transform: S.(Resource<T>) -> S,
  block: suspend () -> T
): Job = resourceFlow { emit(block()) }.update(transform)

fun <S, T> StateScope<S>.produce(
  transform: S.(T) -> S,
  block: suspend () -> T
): Job = launch {
  val value = block()
  update { transform(value) }
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
  val callback: (P1, P2, P3, P4, P5) -> Unit =
    { p1, p2, p3, p4, p5 -> launch { block(p1, p2, p3, p4, p5) } }
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

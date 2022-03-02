/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.state

import com.ivianuu.essentials.resource.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

interface StateScope : CoroutineScope {
  val invalidations: Flow<Unit>

  fun <T> memo(vararg args: Any?, @Inject key: StateKey, init: () -> T): T

  fun invalidate()
}

fun <S> state(
  @Inject scope: CoroutineScope,
  block: StateScope.() -> S
): StateFlow<S> {
  val invalidations = Channel<Unit>(capacity = Channel.UNLIMITED)
  val invalidationsFlow = MutableSharedFlow<Unit>(extraBufferCapacity = Channel.UNLIMITED)

  val stateScope = object : StateScope, CoroutineScope by scope {
    private val states = mutableMapOf<Any, MemoizedState>()
    private var iteration = 0

    override val invalidations = invalidationsFlow

    override fun <T> memo(vararg args: Any?, @Inject key: StateKey, init: () -> T): T {
      val state = states[key]
      return if (state != null) {
        state.lastUsed = iteration
        if (!args.contentEquals(state.args)) {
          val value = init()
          state.args = args
          (state.value as? MemoObserver)?.onDispose()
          state.value = value
          (value as? MemoObserver)?.onInit()
          value
        } else {
          state.value as T
        }
      } else {
        val value = init()
        states[key] = MemoizedState(args, value, iteration)
        (value as? MemoObserver)?.onInit()
        value
      }
    }

    override fun invalidate() {
      launch {
        invalidations.send(Unit)
      }
    }

    fun run(): S {
      iteration++
      return block()
        .also {
          states
            .filterValues { it.lastUsed < iteration }
            .forEach { states.remove(it.key) }
        }
    }
  }

  fun run(): S = stateScope.run()
    .also {
      stateScope.launch {
        invalidationsFlow.emit(Unit)
      }
    }

  return invalidations
    .receiveAsFlow()
    .map { run() }
    .stateIn(scope, SharingStarted.Eagerly, run())
}

fun <T> invalidationFlow(@Inject scope: StateScope, block: (@Inject StateScope) -> T): Flow<T> =
  scope.invalidations
    .onStart { emit(Unit) }
    .map { block() }
    .distinctUntilChanged()

fun <T> memo(vararg args: Any?, @Inject key: StateKey, @Inject scope: StateScope, init: () -> T): T =
  scope.memo(*args, init = init)

fun memoLaunch(vararg args: Any?, @Inject key: StateKey, @Inject scope: StateScope, block: suspend CoroutineScope.() -> Unit) {
  val coroutineScope = memoScope(*args)
  memo(coroutineScope) {
    coroutineScope.launch(block = block)
  }
}

fun memoScope(vararg args: Any?, @Inject key: StateKey, @Inject scope: StateScope): CoroutineScope =
  memo(*args) {
    object : CoroutineScope, MemoObserver {
      override val coroutineContext = scope.coroutineContext + Job(scope.coroutineContext.job)
      override fun onDispose() {
        cancel()
      }
    }
  }

fun <T> Flow<T>.bind(initial: T, vararg args: Any?, @Inject key: StateKey, @Inject scope: StateScope): T {
  val state = memo(*args) { stateVar(initial) }

  memoLaunch(state) {
    collect { state.value = it }
  }

  return state.value
}

fun <T> StateFlow<T>.bind(vararg args: Any?, @Inject key: StateKey, @Inject scope: StateScope): T =
  bind(initial = value, args = *args)

fun <T> Flow<T>.bindResource(vararg args: Any?, @Inject key: StateKey, @Inject scope: StateScope): Resource<T> =
  memo(*args) { flowAsResource() }
    .bind(initial = Idle)

interface ProduceValueScope<T> : CoroutineScope {
  var value: T
}

fun <T> produceValue(
  initial: T,
  vararg args: Any?,
  @Inject key: StateKey,
  @Inject scope: StateScope,
  block: suspend ProduceValueScope<T>.() -> Unit
): T {
  val state = memo(*args) { stateVar(initial) }

  memoLaunch(state) {
    block(
      object : ProduceValueScope<T>, CoroutineScope by scope {
        override var value by state
      }
    )
  }

  return state.value
}

fun <T> produceResource(vararg args: Any?, @Inject key: StateKey, @Inject scope: StateScope, block: suspend () -> T): Resource<T> =
  memo(*args) { resourceFlow { emit(block()) } }.bind(Idle)

fun action(@Inject scope: StateScope, block: suspend () -> Unit): () -> Unit = {
  scope.launch {
    block()
  }
}

fun <P1> action(@Inject scope: StateScope, block: suspend (P1) -> Unit): (P1) -> Unit = { p1 ->
  scope.launch {
    block(p1)
  }
}

fun <P1, P2> action(@Inject scope: StateScope, block: suspend (P1, P2) -> Unit): (P1, P2) -> Unit =
  { p1, p2 ->
    scope.launch {
      block(p1, p2)
    }
  }

fun <P1, P2, P3> action(@Inject scope: StateScope, block: suspend (P1, P2, P3) -> Unit): (P1, P2, P3) -> Unit =
  { p1, p2, p3 ->
    scope.launch {
      block(p1, p2, p3)
    }
  }

fun <P1, P2, P3, P4> action(@Inject scope: StateScope, block: suspend (P1, P2, P3, P4) -> Unit): (P1, P2, P3, P4) -> Unit =
  { p1, p2, p3, p4 ->
    scope.launch {
      block(p1, p2, p3, p4)
    }
  }

fun <P1, P2, P3, P4, P5> action(@Inject scope: StateScope, block: suspend (P1, P2, P3, P4, P5) -> Unit): (P1, P2, P3, P4, P5) -> Unit =
  { p1, p2, p3, p4, p5 ->
    scope.launch {
      block(p1, p2, p3, p4, p5)
    }
  }

fun <T> stateVar(initial: T, @Inject scope: StateScope) = StateVar(initial)

class StateVar<T>(initial: T, @Inject private val scope: StateScope) {
  var value = initial
    set(value) {
      field = value
      scope.invalidate()
    }
}

inline operator fun <T> StateVar<T>.getValue(thisObj: Any?, property: KProperty<*>): T = value

inline operator fun <T> StateVar<T>.setValue(thisObj: Any?, property: KProperty<*>, value: T) {
  this.value = value
}

inline fun <R> withKeys(
  vararg args: Any?,
  @Inject scope: StateScope,
  @Inject key: StateKey,
  block: (@Inject StateKey) -> R
) = block(key + args.map { SourceKey(it.toString()) })

interface MemoObserver {
  fun onInit() {
  }

  fun onDispose() {
  }
}

private class MemoizedState(
  var args: Array<out Any?>,
  var value: Any?,
  var lastUsed: Int
)

typealias StateKey = List<SourceKey>

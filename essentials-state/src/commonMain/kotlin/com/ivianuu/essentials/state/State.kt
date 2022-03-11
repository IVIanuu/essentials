/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.state

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

fun <T> CoroutineScope.state(
  @Inject context: StateContext,
  body: @Composable () -> T
): StateFlow<T> {
  var flow: MutableStateFlow<T>? = null

  state(
    emitter = { value ->
      val outputFlow = flow
      if (outputFlow != null) {
        outputFlow.value = value
      } else {
        flow = MutableStateFlow(value)
      }
    },
    body = body,
  )

  return flow!!
}

private fun <T> CoroutineScope.state(
  emitter: (value: T) -> Unit,
  @Inject stateContext: StateContext,
  body: @Composable () -> T,
) {
  val recomposer = Recomposer(coroutineContext + stateContext)
  val composition = Composition(UnitApplier, recomposer)
  launch(context = stateContext, start = CoroutineStart.UNDISPATCHED) {
    recomposer.runRecomposeAndApplyChanges()
  }

  coroutineContext.job.invokeOnCompletion {
    composition.dispose()
  }

  globalSnapshots

  composition.setContent {
    emitter(body())
  }
}

private val globalSnapshots by lazy {
  var applyScheduled = false
  Snapshot.registerGlobalWriteObserver {
    if (!applyScheduled) {
      applyScheduled = true
      GlobalScope.launch(Dispatchers.Main) {
        applyScheduled = false
        Snapshot.sendApplyNotifications()
      }
    }
  }
}

private object UnitApplier : AbstractApplier<Unit>(Unit) {
  override fun insertBottomUp(index: Int, instance: Unit) {}
  override fun insertTopDown(index: Int, instance: Unit) {}
  override fun move(from: Int, to: Int, count: Int) {}
  override fun remove(index: Int, count: Int) {}
  override fun onClear() {}
}

typealias StateContext = @StateContextTag CoroutineContext

@Tag annotation class StateContextTag

@Provide val stateContext: StateContext by lazy {
  Dispatchers.Main + immediateFrameClock()
}

private fun immediateFrameClock() = object : MonotonicFrameClock {
  override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R =
    onFrame(0L)
}

@Composable fun <T> Flow<T>.bind(initial: T, vararg args: Any?): T {
  val state = remember(*args) { mutableStateOf(initial) }

  LaunchedEffect(state) {
    collect { state.value = it }
  }

  return state.value
}

@Composable fun <T> StateFlow<T>.bind(vararg args: Any?): T =
  bind(initial = value, args = *args)

@Composable fun <T> Flow<T>.bindResource(vararg args: Any?): Resource<T> =
  remember(*args) { flowAsResource() }
    .bind(initial = Idle)

interface ProduceValueScope<T> : CoroutineScope {
  var value: T
}

@Composable fun <T> produceValue(
  initial: T,
  vararg args: Any?,
  block: suspend ProduceValueScope<T>.() -> Unit
): T {
  val state = remember(*args) { mutableStateOf(initial) }

  LaunchedEffect(state) {
    block(
      object : ProduceValueScope<T>, CoroutineScope by this {
        override var value by state
      }
    )
  }

  return state.value
}

@Composable fun <T> produceResource(vararg args: Any?, block: suspend () -> T): Resource<T> =
  remember(*args) { resourceFlow { emit(block()) } }.bind(Idle)

@Composable fun action(block: suspend () -> Unit): () -> Unit {
  val scope = rememberCoroutineScope()
  return {
    scope.launch {
      block()
    }
  }
}

@Composable fun <P1> action(block: suspend (P1) -> Unit): (P1) -> Unit {
  val scope = rememberCoroutineScope()
  return { p1 ->
    scope.launch {
      block(p1)
    }
  }
}

@Composable fun <P1, P2> action(block: suspend (P1, P2) -> Unit): (P1, P2) -> Unit {
  val scope = rememberCoroutineScope()
  return { p1, p2 ->
    scope.launch {
      block(p1, p2)
    }
  }
}

@Composable fun <P1, P2, P3> action(block: suspend (P1, P2, P3) -> Unit): (P1, P2, P3) -> Unit {
  val scope = rememberCoroutineScope()
  return { p1, p2, p3 ->
    scope.launch {
      block(p1, p2, p3)
    }
  }
}

@Composable fun <P1, P2, P3, P4> action(block: suspend (P1, P2, P3, P4) -> Unit): (P1, P2, P3, P4) -> Unit {
  val scope = rememberCoroutineScope()
  return { p1, p2, p3, p4 ->
    scope.launch {
      block(p1, p2, p3, p4)
    }
  }
}

@Composable fun <P1, P2, P3, P4, P5> action(block: suspend (P1, P2, P3, P4, P5) -> Unit): (P1, P2, P3, P4, P5) -> Unit {
  val scope = rememberCoroutineScope()
  return { p1, p2, p3, p4, p5 ->
    scope.launch {
      block(p1, p2, p3, p4, p5)
    }
  }
}

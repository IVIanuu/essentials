/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.state

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

fun <T> CoroutineScope.state(
  @Inject context: StateContext,
  body: @Composable () -> T
): StateFlow<T> {
  val recomposer = Recomposer(coroutineContext + stateContext)
  val composition = Composition(UnitApplier, recomposer)
  launch(stateContext, CoroutineStart.UNDISPATCHED) {
    recomposer.runRecomposeAndApplyChanges()
  }

  var applyScheduled = false
  val snapshotHandle = Snapshot.registerGlobalWriteObserver {
    if (!applyScheduled) {
      applyScheduled = true
      launch(stateContext) {
        applyScheduled = false
        Snapshot.sendApplyNotifications()
      }
    }
  }

  coroutineContext.job.invokeOnCompletion {
    snapshotHandle.dispose()
    composition.dispose()
  }

  var flow: MutableStateFlow<T>? = null
  composition.setContent {
    val value = body()
    val outputFlow = flow
    if (outputFlow != null) {
      outputFlow.value = value
    } else {
      flow = MutableStateFlow(value)
    }
  }

  return flow!!
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

@Composable fun Effect(
  vararg keys: Any?,
  @Inject scope: EffectScope,
  @Inject coroutineScope: EffectCoroutineScope,
  block: suspend CoroutineScope.() -> Unit
) {
  scope.scopeKeyed(currentCompositeKeyHash.toString(), *keys) {
    val job = coroutineScope.launch(block = block)
    Disposable { job.cancel() }
  }
}

@Composable fun <T> Flow<T>.bind(
  initial: T,
  @Inject scope: EffectScope,
  @Inject coroutineScope: EffectCoroutineScope,
  vararg args: Any?
): T {
  val state = scope.scopeKeyed(currentCompositeKeyHash, *args) { mutableStateOf(initial) }

  Effect(state) {
    collect { state.value = it }
  }

  return state.value
}

@Composable fun <T> StateFlow<T>.bind(
  vararg args: Any?,
  @Inject scope: EffectScope,
  @Inject coroutineScope: EffectCoroutineScope
): T = bind(initial = value, args = *args)

@Composable fun <T> Flow<T>.bindResource(
  vararg args: Any?,
  @Inject scope: EffectScope,
  @Inject coroutineScope: EffectCoroutineScope,
): Resource<T> = scope.scopeKeyed(currentCompositeKeyHash.toString(), *args) { flowAsResource() }
  .bind(initial = Idle)

interface ProduceScope<T> : CoroutineScope {
  var value: T
}

@Composable fun <T> produce(
  initial: T,
  vararg args: Any?,
  @Inject scope: Scope<*>,
  @Inject effectScope: EffectCoroutineScope,
  block: suspend ProduceScope<T>.() -> Unit
): T {
  val state = scope.scopeKeyed(currentCompositeKeyHash.toString(), *args) { mutableStateOf(initial) }

  Effect(state) {
    block(
      object : ProduceScope<T>, CoroutineScope by this {
        override var value by state
      }
    )
  }

  return state.value
}

@Composable fun <T> produceResource(
  vararg args: Any?,
  @Inject scope: EffectScope,
  @Inject coroutineScope: EffectCoroutineScope,
  block: suspend () -> T
): Resource<T> = scope.scopeKeyed(currentCompositeKeyHash.toString(), *args) {
  resourceFlow { emit(block()) }
}.bind(Idle)

@Composable fun action(
  @Inject coroutineScope: EffectCoroutineScope,
  block: suspend () -> Unit
): () -> Unit = {
  coroutineScope.launch {
    block()
  }
}

@Composable fun <P1> action(
  @Inject coroutineScope: EffectCoroutineScope,
  block: suspend (P1) -> Unit
): (P1) -> Unit = { p1 ->
  coroutineScope.launch {
    block(p1)
  }
}

@Composable fun <P1, P2> action(
  @Inject coroutineScope: EffectCoroutineScope,
  block: suspend (P1, P2) -> Unit
): (P1, P2) -> Unit = { p1, p2 ->
  coroutineScope.launch {
    block(p1, p2)
  }
}

@Composable fun <P1, P2, P3> action(
  @Inject coroutineScope: EffectCoroutineScope,
  block: suspend (P1, P2, P3) -> Unit
): (P1, P2, P3) -> Unit = { p1, p2, p3 ->
  coroutineScope.launch {
    block(p1, p2, p3)
  }
}

@Composable fun <P1, P2, P3, P4> action(
  @Inject coroutineScope: EffectCoroutineScope,
  block: suspend (P1, P2, P3, P4) -> Unit
): (P1, P2, P3, P4) -> Unit = { p1, p2, p3, p4 ->
  coroutineScope.launch {
    block(p1, p2, p3, p4)
  }
}

@Composable fun <P1, P2, P3, P4, P5> action(
  @Inject coroutineScope: EffectCoroutineScope,
  block: suspend (P1, P2, P3, P4, P5) -> Unit
): (P1, P2, P3, P4, P5) -> Unit = { p1, p2, p3, p4, p5 ->
  coroutineScope.launch {
    block(p1, p2, p3, p4, p5)
  }
}

@Tag annotation class EffectScopeTag {
  companion object {
    @Provide @Composable fun provideRememberedScope(): EffectScope = remember { Scope<Any>() }
  }
}

typealias EffectScope = @EffectScopeTag Scope<*>

@Tag annotation class EffectCoroutineScopeTag {
  sealed interface BaseModule {
    @Provide @Composable fun composableEffectScope() = rememberCoroutineScope()
  }
  companion object : BaseModule {
    @Provide fun namedAsEffectScope(
      scope: NamedCoroutineScope<*>,
      stateContext: StateContext
    ): EffectCoroutineScope = scope.childCoroutineScope(stateContext)
  }
}

typealias EffectCoroutineScope = @EffectCoroutineScopeTag CoroutineScope

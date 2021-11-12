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

package com.ivianuu.essentials.ui.state

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.Snapshot
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.flowAsResource
import com.ivianuu.injekt.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

fun <T> composedFlow(body: @Composable () -> T) = channelFlow<T> {
  composedState(
    emitter = { trySend(it).getOrThrow() },
    body = body,
  )
}

fun <T> composedFlow(
  @Inject S: CoroutineScope,
  body: @Composable () -> T
): StateFlow<T> {
  var flow: MutableStateFlow<T>? = null

  composedState(
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

fun <T> composedState(
  emitter: (value: T) -> Unit,
  @Inject scope: CoroutineScope,
  body: @Composable () -> T,
) {
  val recomposer = Recomposer(scope.coroutineContext)
  val composition = Composition(UnitApplier, recomposer)
  launch(start = CoroutineStart.UNDISPATCHED) {
    recomposer.runRecomposeAndApplyChanges()
  }

  var applyScheduled = false
  val snapshotHandle = Snapshot.registerGlobalWriteObserver {
    if (!applyScheduled) {
      applyScheduled = true
      launch {
        applyScheduled = false
        Snapshot.sendApplyNotifications()
      }
    }
  }
  scope.coroutineContext.job.invokeOnCompletion {
    snapshotHandle.dispose()
  }

  composition.setContent {
    emitter(body())
  }
}

private object UnitApplier : AbstractApplier<Unit>(Unit) {
  override fun insertBottomUp(index: Int, instance: Unit) {}
  override fun insertTopDown(index: Int, instance: Unit) {}
  override fun move(from: Int, to: Int, count: Int) {}
  override fun remove(index: Int, count: Int) {}
  override fun onClear() {}
}

@Composable fun <T> Flow<T>.state(initial: T) = collectAsState(initial).value

@Composable fun <T> StateFlow<T>.state() = collectAsState(value).value

@Composable fun <T> Flow<T>.resourceState() =
  remember { flowAsResource() }.collectAsState(Idle).value

@Composable fun action(block: suspend () -> Unit): () -> Unit {
  val scope = rememberCoroutineScope()
  return remember {
    {
      scope.launch {
        block()
      }
    }
  }
}

@Composable fun <P1> action(block: suspend (P1) -> Unit): (P1) -> Unit {
  val scope = rememberCoroutineScope()
  return remember {
    { p1 ->
      scope.launch {
        block(p1)
      }
    }
  }
}

@Composable fun <P1, P2> action(block: suspend (P1, P2) -> Unit): (P1, P2) -> Unit {
  val scope = rememberCoroutineScope()
  return remember {
    { p1, p2 ->
      scope.launch {
        block(p1, p2)
      }
    }
  }
}

@Composable fun <P1, P2, P3> action(block: suspend (P1, P2, P3) -> Unit): (P1, P2, P3) -> Unit {
  val scope = rememberCoroutineScope()
  return remember {
    { p1, p2, p3 ->
      scope.launch {
        block(p1, p2, p3)
      }
    }
  }
}

@Composable fun <P1, P2, P3, P4> action(block: suspend (P1, P2, P3, P4) -> Unit): (P1, P2, P3, P4) -> Unit {
  val scope = rememberCoroutineScope()
  return remember {
    { p1, p2, p3, p4 ->
      scope.launch {
        block(p1, p2, p3, p4)
      }
    }
  }
}

@Composable fun <P1, P2, P3, P4, P5> action(block: suspend (P1, P2, P3, P4, P5) -> Unit): (P1, P2, P3, P4, P5) -> Unit {
  val scope = rememberCoroutineScope()
  return remember {
    { p1, p2, p3, p4, p5 ->
      scope.launch {
        block(p1, p2, p3, p4, p5)
      }
    }
  }
}

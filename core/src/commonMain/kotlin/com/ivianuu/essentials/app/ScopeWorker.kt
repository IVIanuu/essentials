/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import androidx.compose.runtime.*
import app.cash.molecule.*
import arrow.fx.coroutines.*
import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

fun interface ScopeWorker<N : Any> : ExtensionPoint<ScopeWorker<N>> {
  suspend fun doWork()
}

fun interface ScopeComposition<N : Any> : ScopeWorker<N> {
  @Composable fun Content()

  override suspend fun doWork() {
    coroutineScope { launchMolecule(RecompositionMode.Immediate, {}) { Content() } }
  }
}

class ScopeWorkerManager<N : Any> @Provide @Service<N> @Scoped<N> constructor(
  private val coroutineScope: ScopedCoroutineScope<N>,
  private val logger: Logger,
  private val nameKey: KClass<N>,
  private val workersFactory: () -> List<ExtensionPointRecord<ScopeWorker<N>>>,
) : ScopeObserver<N> {
  var state by mutableStateOf(State.IDLE)
    private set

  override fun onEnter(scope: Scope<N>) {
    coroutineScope.launch {
      guaranteeCase(
        fa = {
          supervisorScope {
            val workers = workersFactory()
              .sortedWithLoadingOrder()

            logger.d { "${nameKey.simpleName} run scope workers ${workers.map { it.key.qualifiedName }}" }

            val jobs = workers.map { record ->
              launch(start = CoroutineStart.UNDISPATCHED) { record.instance.doWork() }
            }

            launch { state = State.RUNNING }

            try {
              jobs.joinAll()
            } finally {
              state = State.COMPLETED
            }
          }

          logger.d { "${nameKey.simpleName} scope workers completed" }
        },
        finalizer = {
          if (it is ExitCase.Cancelled)
            logger.d { "${nameKey.simpleName} cancel scope workers" }
        }
      )
    }
  }

  enum class State { IDLE, RUNNING, COMPLETED }

  @Provide companion object {
    @Provide fun <N : Any> loadingOrder() = LoadingOrder<ScopeWorkerManager<N>>()
      .after<ScopeInitializerRunner<N>>()
  }
}

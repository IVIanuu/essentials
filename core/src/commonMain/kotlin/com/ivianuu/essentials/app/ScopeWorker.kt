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
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*

fun interface ScopeWorker<N> : ExtensionPoint<ScopeWorker<N>> {
  suspend operator fun invoke()
}

fun <N> ScopeComposition(block: @Composable () -> Unit) = ScopeWorker<N> {
  coroutineScope { launchMolecule(RecompositionMode.Immediate,{}, body = block) }
}

@Provide class ScopeWorkerRunner<N>(
  private val coroutineScope: ScopedCoroutineScope<N>,
  private val logger: Logger,
  private val nameKey: TypeKey<N>,
  private val workersFactory: () -> List<ExtensionPointRecord<ScopeWorker<N>>>
) : ScopeObserver<N> {
  override fun onEnter(scope: Scope<N>) {
    coroutineScope.launch {
      guaranteeCase(
        fa = {
          supervisorScope {
            val workers = workersFactory()
              .sortedWithLoadingOrder()

            logger.d { "${nameKey.value} run scope workers ${workers.map { it.key.value }}" }

            workers.forEach { record ->
              launch { record.instance() }
            }
          }

          logger.d { "${nameKey.value} scope workers completed" }
        },
        finalizer = {
          if (it is ExitCase.Cancelled)
            logger.d { "${nameKey.value} cancel scope workers" }
        }
      )
    }
  }

  @Provide companion object {
    @Provide fun <N> loadingOrder(@Inject nameKey: TypeKey<N>) = LoadingOrder<ScopeWorkerRunner<N>>()
      .after<ScopeInitializerRunner<N>>()
  }
}

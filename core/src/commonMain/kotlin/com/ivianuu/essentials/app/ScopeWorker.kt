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
import kotlinx.coroutines.*
import kotlin.reflect.*

fun interface ScopeWorker<N : Any> : ExtensionPoint<ScopeWorker<N>> {
  suspend operator fun invoke()
}

fun <N : Any> ScopeComposition(block: @Composable () -> Unit) = ScopeWorker<N> {
  coroutineScope { launchMolecule(RecompositionMode.Immediate,{}, body = block) }
}

@Provide class ScopeWorkerRunner<N : Any>(
  private val coroutineScope: ScopedCoroutineScope<N>,
  private val logger: Logger,
  private val nameKey: KClass<N>,
  private val workersFactory: () -> List<ExtensionPointRecord<ScopeWorker<N>>>
) : ScopeObserver<N> {
  override fun onEnter(scope: Scope<N>) {
    coroutineScope.launch {
      guaranteeCase(
        fa = {
          supervisorScope {
            val workers = workersFactory()
              .sortedWithLoadingOrder()

            logger.d { "${nameKey.simpleName} run scope workers ${workers.map { it.key.qualifiedName }}" }

            workers.forEach { record ->
              launch { record.instance() }
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

  @Provide companion object {
    @Provide fun <N : Any> loadingOrder() = LoadingOrder<ScopeWorkerRunner<N>>()
      .after<ScopeInitializerRunner<N>>()
  }
}

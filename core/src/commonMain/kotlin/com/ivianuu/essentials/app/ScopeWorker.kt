/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import androidx.compose.runtime.Composable
import com.ivianuu.essentials.ExtensionPoint
import com.ivianuu.essentials.ExtensionPointRecord
import com.ivianuu.essentials.LoadingOrder
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.ScopeObserver
import com.ivianuu.essentials.compose.StateCoroutineContext
import com.ivianuu.essentials.compose.launchComposition
import com.ivianuu.essentials.coroutines.ExitCase
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.sortedWithLoadingOrder
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

fun interface ScopeWorker<N> : ExtensionPoint<ScopeWorker<N>> {
  suspend operator fun invoke()
}

fun <N> ScopeComposition(
  @Inject stateCoroutineContext: StateCoroutineContext,
  block: @Composable () -> Unit
) = ScopeWorker<N> {
  coroutineScope { launchComposition(block = block) }
}

interface ScopeWorkerRunner<N> : ScopeObserver<N> {
  @Provide companion object {
    @Provide fun <N> loadingOrder(@Inject nameKey: TypeKey<N>) = LoadingOrder<ScopeWorkerRunner<N>>()
      .after<ScopeInitializerRunner<N>>()
  }
}

@Provide fun <N> scopeWorkerRunner(
  coroutineScope: ScopedCoroutineScope<N>,
  logger: Logger,
  nameKey: TypeKey<N>,
  workersFactory: () -> List<ExtensionPointRecord<ScopeWorker<N>>>
): ScopeWorkerRunner<N> = object : ScopeWorkerRunner<N> {
  override fun onEnter(scope: Scope<N>) {
    coroutineScope.launch {
      guarantee(
        block = {
          supervisorScope {
            val workers = workersFactory()
              .sortedWithLoadingOrder()

            logger.log { "${nameKey.value} run scope workers ${workers.map { it.key.value }}" }

            workers.forEach { record ->
              launch { record.instance() }
            }
          }

          logger.log { "${nameKey.value} scope workers completed" }
        },
        finalizer = {
          if (it is ExitCase.Cancelled)
            logger.log { "${nameKey.value} cancel scope workers" }
        }
      )
    }
  }
}

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import androidx.compose.runtime.Composable
import com.ivianuu.essentials.compose.launchComposition
import com.ivianuu.essentials.coroutines.ExitCase
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

fun interface ScopeWorker<N> : ExtensionPoint<ScopeWorker<N>> {
  suspend operator fun invoke()
}

fun <N> ScopeComposition(block: @Composable () -> Unit) = ScopeWorker<N> {
  coroutineScope { launchComposition(block = block) }
}

fun interface ScopeWorkerRunner<N> {
  operator fun invoke()
}

@Provide fun <N> scopeWorkerRunner(
  logger: Logger,
  nameKey: TypeKey<N>,
  scope: ScopedCoroutineScope<N>,
  workers: () -> List<ExtensionPointRecord<ScopeWorker<N>>>
) = ScopeWorkerRunner<N> {
  scope.launch {
    guarantee(
      block = {
        supervisorScope {
          logger.log { "${nameKey.value} run scope workers" }

          workers()
            .sortedWithLoadingOrder()
            .forEach { record ->
              launch {
                record.instance()
              }
            }
        }
      },
      finalizer = {
        if (it is ExitCase.Cancelled)
          logger.log { "${nameKey.value} cancel scope workers" }
      }
    )
  }
}

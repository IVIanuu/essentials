/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

fun interface ScopeWorker<N> : suspend () -> Unit

@Provide fun <N> defaultScopeWorkers() = emptyList<ScopeWorker<N>>()

fun interface ScopeWorkerRunner<N> : () -> Unit

@Provide fun <N> scopeWorkerRunner(
  scope: NamedCoroutineScope<N>,
  nameKey: TypeKey<N>,
  workers: () -> List<ScopeWorker<N>>,
  L: Logger
) = ScopeWorkerRunner<N> {
  log { "${nameKey.value} run scope workers" }
  scope.launch {
    guarantee(
      block = {
        supervisorScope {
          workers()
            .forEach { worker ->
              launch {
                worker()
              }
            }
        }
      },
      finalizer = {
        log { "${nameKey.value} cancel scope workers" }
      }
    )
  }
}

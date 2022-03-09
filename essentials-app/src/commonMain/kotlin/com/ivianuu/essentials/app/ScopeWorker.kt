/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import kotlinx.coroutines.*

fun interface ScopeWorker<N> : suspend () -> Unit

fun interface ScopeWorkerRunner<N> : () -> Unit

fun <N> scopeWorkerRunner(
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

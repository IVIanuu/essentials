/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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

fun interface ScopeWorker<N> : suspend () -> Unit, Service<ScopeWorker<N>>

fun interface ScopeWorkerRunner<N> : () -> Unit

context(Logger, NamedCoroutineScope<N>) @Provide fun <N> scopeWorkerRunner(
  nameKey: TypeKey<N>,
  workers: () -> List<ServiceElement<ScopeWorker<N>>>
) = ScopeWorkerRunner<N> {
  log { "${nameKey.value} run scope workers" }
  launch {
    guarantee(
      block = {
        supervisorScope {
          workers()
            .sortedWithLoadingOrder()
            .forEach { worker ->
              launch {
                worker.instance()
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

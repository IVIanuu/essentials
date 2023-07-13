/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.coroutines.ExitCase
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

fun interface ScopeWorker<N> : ExtensionPoint<ScopeWorker<N>> {
  suspend operator fun invoke()
}

fun interface ScopeWorkerRunner<N> {
  operator fun invoke()
}

context(Logger) @Provide fun <N> scopeWorkerRunner(
  nameKey: TypeKey<N>,
  scope: ScopedCoroutineScope<N>,
  workers: () -> List<ExtensionPointRecord<ScopeWorker<N>>>
) = ScopeWorkerRunner<N> {
  scope.launch {
    guarantee(
      block = {
        supervisorScope {
          log { "${nameKey.value} run scope workers" }

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
          log { "${nameKey.value} cancel scope workers" }
      }
    )
  }
}

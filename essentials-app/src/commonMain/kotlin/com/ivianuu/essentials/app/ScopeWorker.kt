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

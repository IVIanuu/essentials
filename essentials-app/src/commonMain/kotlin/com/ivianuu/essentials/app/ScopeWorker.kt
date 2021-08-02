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

import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*

typealias ScopeWorker<S> = suspend () -> Unit

typealias ScopeWorkerRunner<S> = () -> Unit

@Provide fun <S : Scope> scopeWorkerRunner(
  logger: Logger,
  scope: InjektCoroutineScope<S>,
  scopeKey: TypeKey<S>,
  workers: Set<() -> ScopeWorker<S>> = emptySet()
): ScopeWorkerRunner<S> = {
  d { "${scopeKey.value} run scope workers" }
  scope.launch {
    runWithCleanup(
      block = {
        supervisorScope {
          workers
            .forEach { worker ->
              launch {
                worker()()
              }
            }
        }
      },
      cleanup = {
        d { "${scopeKey.value} cancel scope workers" }
      }
    )
  }
}

/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.foreground

import androidx.work.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.work.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

@Provide fun androidForegroundWorkerStarter(
  logger: Logger,
  state: Flow<InternalForegroundState>,
  workManager: WorkManager
): ScopeWorker<AppScope> = {
  state
    .filter { it.isForeground }
    .filter {
      workManager.getWorkInfosByTag(ForegroundWorkerId.toFunctionalWorkerTag())
        .await()
        .none { it.state == WorkInfo.State.RUNNING }
    }
    .collect {
      d { "start foreground worker $it" }
      workManager.cancelAllWorkByTag(ForegroundWorkerId.toFunctionalWorkerTag())
      workManager.enqueue(
        OneTimeWorkRequestBuilder(ForegroundWorkerId)
          .build()
      )
    }
}

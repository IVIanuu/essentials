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

package com.ivianuu.essentials.foreground

import android.app.NotificationManager
import androidx.work.ListenableWorker
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.work.Worker
import com.ivianuu.essentials.work.WorkerId
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile

@Provide object ForegroundWorkerId : WorkerId("foreground")

@Provide fun foregroundWorker(
  internalForegroundState: Flow<InternalForegroundState>,
  logger: Logger,
  notificationManager: @SystemService NotificationManager
): Worker<ForegroundWorkerId> = {
  log { "start foreground worker" }

  suspend fun applyState(infos: List<ForegroundInfo>) {
    log { "apply infos: $infos" }

    infos
      .filter { it.state is ForegroundState.Background }
      .forEach { notificationManager.cancel(it.id) }

    if (infos.any { it.state is ForegroundState.Foreground }) {
      infos
        .mapNotNull { info ->
          (info.state as? ForegroundState.Foreground)?.let {
            info.id to it.notification
          }
        }
        .forEachIndexed { index, (id, notification) ->
          if (index == 0) {
            setForeground(
              androidx.work.ForegroundInfo(
                id,
                notification
              )
            )
          } else {
            notificationManager.notify(id, notification)
          }
        }
    }
  }

  guarantee(
    block = {
      internalForegroundState
        .map { it.infos }
        .takeWhile { infos -> infos.any { info -> info.state is ForegroundState.Foreground } }
        .collect { applyState(it) }
    },
    finalizer = {
      applyState(emptyList())
    }
  )

  log { "stop foreground worker" }

  ListenableWorker.Result.success()
}

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

import android.app.Notification
import android.app.NotificationManager
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.service.EsService
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ForegroundService : EsService() {

    private val component by lazy {
        serviceComponent.mergeComponent<ForegroundServiceComponent>()
    }

    private var lastJobs = listOf<ForegroundJob>()

    override fun onCreate() {
        super.onCreate()
        component.logger.d { "started foreground service" }
        component.foregroundManager.jobs
            .flatMapLatest { jobs ->
                if (jobs.isNotEmpty()) {
                    combine(jobs.map { it.notification }) { notifications ->
                        jobs.zip(notifications)
                    }
                } else {
                    flowOf(emptyList())
                }
            }
            .onEach { update(it) }
            .launchIn(scope)

        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            runOnCancellation {
                component.logger.d { "stopped foreground service" }
                update(emptyList())
            }
        }
    }

    private fun update(newJobs: List<Pair<ForegroundJob, Notification>>) {
        component.logger.d { "update jobs: $newJobs last: $lastJobs" }

        lastJobs
            .filter { prevJob ->
                newJobs.none { prevJob == it.first }
            }
            .forEach { job -> component.notificationManager.cancel(job.id) }

        lastJobs = newJobs.map { it.first }

        if (newJobs.isNotEmpty()) {
            newJobs.forEachIndexed { index, (job, notification) ->
                if (index == 0) {
                    startForeground(job.id, notification)
                } else {
                    component.notificationManager.notify(job.id, notification)
                }
            }
        } else {
            stopForeground(true)
            stopSelf()
        }
    }
}

@MergeInto(ServiceComponent::class)
interface ForegroundServiceComponent {
    val foregroundManager: ForegroundManager
    val notificationManager: NotificationManager
    val logger: Logger
}

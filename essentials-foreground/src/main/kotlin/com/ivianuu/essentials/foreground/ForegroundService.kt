/*
 * Copyright 2019 Manuel Wrage
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
import com.ivianuu.essentials.service.EsService
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ForegroundService : EsService() {

    private val component by lazy {
        serviceComponent.mergeComponent<ForegroundServiceComponent>()
    }

    private var lastJobs = listOf<ForegroundJob>()
    private var foregroundId: Int? = null

    override fun onCreate() {
        super.onCreate()

        component.logger.d("started foreground service")

        component.foregroundManager.jobs
            .onEach { update(it) }
            .launchIn(scope)
    }

    override fun onDestroy() {
        super.onDestroy()
        component.logger.d("stopped foreground service")
    }

    private fun update(newJobs: List<ForegroundJob>) = synchronized(this) {
        component.logger.d("update jobs $newJobs")

        lastJobs
            .filter { it !in newJobs }
            .forEach { job ->
                component.notificationManager.cancel(job.id)
                if (job.id == foregroundId) {
                    foregroundId = null
                }
            }

        lastJobs = newJobs

        if (newJobs.isNotEmpty()) {
            newJobs.forEachIndexed { index, job ->
                if (index == 0) {
                    startForeground(job.id, job.notification)
                } else {
                    component.notificationManager.notify(job.id, job.notification)
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
    val foregroundManager: RealForegroundManager
    val notificationManager: NotificationManager
    val logger: Logger
}

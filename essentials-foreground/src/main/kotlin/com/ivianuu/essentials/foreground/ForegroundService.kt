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
import com.ivianuu.injekt.given
import com.ivianuu.injekt.runReader
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class ForegroundService : EsService() {

    private val foregroundManager: ForegroundManager by lazy {
        component.runReader { given() }
    }
    private val logger: Logger by lazy {
        component.runReader { given() }
    }
    private val notificationManager: NotificationManager by lazy {
        component.runReader { given() }
    }

    private var lastJobs = listOf<ForegroundJob>()
    private var foregroundId: Int? = null

    override fun onCreate() {
        super.onCreate()

        logger.d("started foreground service")

        foregroundManager.updates
            .onStart { emit(Unit) }
            .onEach { update() }
            .launchIn(scope)

        foregroundManager.stopServiceRequests
            .onEach { stop() }
            .launchIn(scope)
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.d("stopped foreground service")
    }

    private fun update() = synchronized(this) {
        val newJobs = foregroundManager.job

        logger.d("update jobs $newJobs")

        lastJobs
            .filter { it !in newJobs }
            .forEach { job ->
                notificationManager.cancel(job.id)
                if (job.id == foregroundId) {
                    foregroundId = null
                }
            }

        lastJobs = newJobs
        if (newJobs.isEmpty()) return

        newJobs.forEachIndexed { index, job ->
            if (index == 0) {
                startForeground(job.id, job.notification)
            } else {
                notificationManager.notify(job.id, job.notification)
            }
        }
    }

    private fun stop() = synchronized(this) {
        lastJobs.forEach { notificationManager.cancel(it.id) }
        stopForeground(true)
        stopSelf()
    }
}

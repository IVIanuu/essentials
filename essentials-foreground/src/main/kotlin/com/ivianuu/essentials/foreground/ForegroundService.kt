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
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.android.runServiceReader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ForegroundService : EsService() {

    private val foregroundManager: ForegroundManager by lazy {
        runServiceReader { given() }
    }
    private val notificationManager: NotificationManager by lazy {
        runServiceReader { given() }
    }

    private var lastJobs = listOf<ForegroundJob>()
    private var foregroundId: Int? = null

    override fun onCreate() {
        super.onCreate()

        runServiceReader {
            d { "started foreground service" }
        }

        foregroundManager.jobs
            .onEach { update(it) }
            .launchIn(scope)
    }

    override fun onDestroy() {
        super.onDestroy()
        runServiceReader {
            d { "stopped foreground service" }
        }
    }

    private fun update(newJobs: List<ForegroundJob>) = synchronized(this) {
        runServiceReader {
            d { "update jobs $newJobs" }
        }

        lastJobs
            .filter { it !in newJobs }
            .forEach { job ->
                notificationManager.cancel(job.id)
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
                    notificationManager.notify(job.id, job.notification)
                }
            }
        } else {
            stopForeground(true)
            stopSelf()
        }
    }

}

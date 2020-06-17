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

import android.app.Notification
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ApplicationScoped
import com.ivianuu.injekt.ForApplication
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.atomic.AtomicInteger

@ApplicationScoped
class ForegroundManager(
    private val context: @ForApplication Context,
    private val logger: Logger
) {

    private val _updates = EventFlow<Unit>()
    val updates: Flow<Unit> get() = _updates

    private val _jobs = mutableListOf<ForegroundJob>()
    val job: List<ForegroundJob> get() = _jobs.toList()

    private val _stopServiceRequests =
        EventFlow<Unit>()
    internal val stopServiceRequests: Flow<Unit> get() = _stopServiceRequests

    fun startJob(notification: Notification): ForegroundJob = synchronized(this) {
        val job = object : ForegroundJob {

            override val id: Int = ids.incrementAndGet()

            override val isActive: Boolean
                get() = synchronized(this@ForegroundManager) { this in _jobs }

            override var notification = notification

            override fun updateNotification(notification: Notification) {
                this.notification = notification
                updateServiceState()
                dispatchUpdate()
            }

            override fun stop() {
                stopJob(this)
            }
        }

        logger.d("start job $job")

        _jobs += job
        updateServiceState()
        dispatchUpdate()

        return@synchronized job
    }

    fun stopJob(job: ForegroundJob) = synchronized(this) {
        if (job !in _jobs) return@synchronized
        logger.d("stop job $job")
        _jobs -= job
        updateServiceState()
        dispatchUpdate()
    }

    private fun dispatchUpdate() {
        _updates.offer(Unit)
    }

    private fun updateServiceState() = synchronized(this) {
        logger.d("update service state $_jobs")
        if (_jobs.isNotEmpty()) {
            logger.d("start foreground service")
            ContextCompat.startForegroundService(
                context,
                Intent(context, ForegroundService::class.java)
            )
        } else {
            logger.d("stop foreground service")
            _stopServiceRequests.offer(Unit)
        }
    }

}

private val ids = AtomicInteger(0)

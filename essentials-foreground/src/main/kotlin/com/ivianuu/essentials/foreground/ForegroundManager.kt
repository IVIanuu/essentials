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
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.android.ApplicationContext
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.atomic.AtomicInteger

@Given(ApplicationComponent::class)
@Reader
class ForegroundManager {

    private val _updates = EventFlow<Unit>()
    val updates: Flow<Unit> get() = _updates

    private val _jobs = mutableListOf<ForegroundJob>()
    val job: List<ForegroundJob> get() = _jobs.toList()

    private val _stopServiceRequests = EventFlow<Unit>()
    internal val stopServiceRequests: Flow<Unit> get() = _stopServiceRequests

    fun startJob(notification: Notification): ForegroundJob = synchronized(this) {
        val job = ForegroundJobImpl(notification)
        _jobs += job
        d { "start job $job" }
        updateServiceState()
        dispatchUpdate()

        return@synchronized job
    }

    fun stopJob(job: ForegroundJob) = synchronized(this) {
        if (job !in _jobs) return@synchronized
        _jobs -= job
        d { "stop job $job" }
        updateServiceState()
        dispatchUpdate()
    }

    private fun dispatchUpdate() {
        _updates.offer(Unit)
    }

    private fun updateServiceState() = synchronized(this) {
        d { "update service state $_jobs" }
        if (_jobs.isNotEmpty()) {
            ContextCompat.startForegroundService(
                applicationContext,
                Intent(applicationContext, ForegroundService::class.java)
            )
        } else {
            _stopServiceRequests.offer(Unit)
        }
    }

    private inner class ForegroundJobImpl(
        override var notification: Notification
    ) : ForegroundJob {

        override val id: Int = ids.incrementAndGet()

        override val isActive: Boolean
            get() = synchronized(this@ForegroundManager) { this in _jobs }

        override fun updateNotification(notification: Notification) {
            this.notification = notification
            updateServiceState()
            dispatchUpdate()
        }

        override fun stop() {
            stopJob(this)
        }

        override fun toString(): String {
            return "ForegroundJobImpl(id=$id, isActive=$isActive)"
        }
    }
}

private val ids = AtomicInteger(0)

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
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ivianuu.essentials.util.DefaultDispatcher
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ImplBinding
import com.ivianuu.injekt.android.ApplicationContext
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicInteger

interface ForegroundManager {
    fun startJob(notification: Notification): ForegroundJob
}

@ImplBinding(ApplicationComponent::class)
class ForegroundManagerImpl(
    private val applicationContext: ApplicationContext,
    private val defaultDispatcher: DefaultDispatcher,
    private val logger: Logger,
) : ForegroundManager {

    private val _jobs = MutableStateFlow(emptyList<ForegroundJob>())
    internal val jobs: StateFlow<List<ForegroundJob>> get() = _jobs

    override fun startJob(notification: Notification): ForegroundJob {
        val job = ForegroundJobImpl(notification)
        _jobs.value += job
        logger.d("start job $job")
        startServiceIfNeeded()
        dispatchUpdate()
        return job
    }

    private fun dispatchUpdate() {
        _jobs.value = _jobs.value
    }

    private fun startServiceIfNeeded() {
        if (_jobs.value.isNotEmpty()) {
            logger.d("start service ${_jobs.value}")
            ContextCompat.startForegroundService(
                applicationContext,
                Intent(applicationContext, ForegroundService::class.java)
            )
        }
    }

    private inner class ForegroundJobImpl(
        override var notification: Notification
    ) : ForegroundJob {

        override val scope = CoroutineScope(defaultDispatcher)

        override val id: Int = ids.incrementAndGet()

        override val isActive: Boolean
            get() = this in _jobs.value

        override fun updateNotification(notification: Notification) {
            this.notification = notification
            dispatchUpdate()
        }

        override fun stop() {
            if (this !in _jobs.value) return
            scope.cancel()
            _jobs.value -= this
            logger.d("stop job $this")
            dispatchUpdate()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ForegroundJobImpl

            if (id != other.id) return false
            if (notification != other.notification) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id
            result = 31 * result + notification.hashCode()
            return result
        }

        override fun toString(): String {
            return "ForegroundJobImpl(id=$id, isActive=$isActive)"
        }
    }
}

private val ids = AtomicInteger(0)

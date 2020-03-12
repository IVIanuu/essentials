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
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.service.EsService
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.getLazy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class ForegroundService : EsService() {

    private val dispatchers: AppCoroutineDispatchers by getLazy()
    private val foregroundManager: ForegroundManager by getLazy()
    private var lastComponents = listOf<ForegroundComponent>()
    private var foregroundId: Int? = null
    private val notificationManager: NotificationManager by getLazy()

    override fun onCreate() {
        super.onCreate()

        d { "start" }

        foregroundManager.updates
            .onStart { emit(Unit) }
            .onEach { update() }
            .launchIn(coroutineScope)

        foregroundManager.stopServiceRequests
            .onEach { stop() }
            .launchIn(coroutineScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        d { "stop" }
    }

    private suspend fun update() {
        val newComponents = foregroundManager.components

        d { "update components $newComponents" }

        lastComponents
            .filter { it !in newComponents }
            .forEach { component ->
                notificationManager.cancel(component.id)
                if (component.id == foregroundId) {
                    foregroundId = null
                }
            }

        lastComponents = newComponents
        if (newComponents.isEmpty()) return

        newComponents.forEachIndexed { index, component ->
            val notification = withContext(dispatchers.main) {
                component.notificationFactory.buildNotification()
            }
            if (index == 0) {
                startForeground(component.id, notification)
            } else {
                notificationManager.notify(component.id, notification)
            }
        }
    }

    private fun stop() {
        lastComponents.forEach {
            notificationManager.cancel(it.id)
        }
        stopForeground(true)
        stopSelf()
    }
}

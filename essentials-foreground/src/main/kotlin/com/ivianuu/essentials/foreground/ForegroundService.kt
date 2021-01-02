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

import android.app.NotificationManager
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.foreground.ForegroundState.*
import com.ivianuu.essentials.service.EsService
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.component.ComponentElementBinding
import com.ivianuu.injekt.component.get
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ForegroundService : EsService() {

    private val component by lazy {
        serviceComponent.get<ForegroundServiceComponent>()
    }

    override fun onCreate() {
        super.onCreate()
        component.logger.d { "started foreground service" }
        component.internalForegroundState
            .map { it.infos }
            .onEach { applyState(it) }
            .launchIn(scope)

        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            runOnCancellation {
                component.logger.d { "stopped foreground service" }
                applyState(emptyList())
            }
        }
    }

    private fun applyState(infos: List<ForegroundInfo>) {
        component.logger.d { "apply infos: $infos" }

        infos
            .filter { it.state is Background }
            .forEach { component.notificationManager.cancel(it.id) }

        if (infos.any { it.state is Foreground }) {
            infos
                .filter { it.state is Foreground }
                .map { it.id to (it.state as Foreground).notification }
                .forEachIndexed { index, (id, notification) ->
                    if (index == 0) {
                        startForeground(id, notification)
                    } else {
                        component.notificationManager.notify(id, notification)
                    }
                }
        } else {
            stopForeground(true)
            stopSelf()
        }
    }
}

@ComponentElementBinding<ServiceComponent>
@Given class ForegroundServiceComponent(
    @Given val internalForegroundState: Flow<InternalForegroundState>,
    @Given val notificationManager: NotificationManager,
    @Given val logger: Logger
)

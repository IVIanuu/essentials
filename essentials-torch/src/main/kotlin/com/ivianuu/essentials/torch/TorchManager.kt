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

package com.ivianuu.essentials.torch

import android.hardware.camera2.CameraManager
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.foreground.ForegroundJob
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.ApplicationScoped
import com.ivianuu.injekt.ForApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/**
 * Provides the torch state
 */
@ApplicationScoped
class TorchManager internal constructor(
    broadcastFactory: BroadcastFactory,
    private val cameraManager: CameraManager,
    private val scope: @ForApplication CoroutineScope,
    private val dispatchers: AppCoroutineDispatchers,
    private val foregroundManager: ForegroundManager,
    private val notificationFactory: TorchNotificationFactory,
    private val logger: Logger,
    private val toaster: Toaster
) {

    private val _torchState = MutableStateFlow(false)
    val torchState: StateFlow<Boolean> get() = _torchState

    private var foregroundJob: ForegroundJob? = null

    private val stateActor = scope.actor<Boolean>(dispatchers.computation) {
        for (enabled in this) {
            logger.d("update state $enabled")
            foregroundJob = if (enabled) {
                foregroundManager.startJob(notificationFactory.create())
            } else {
                foregroundJob?.stop()
                null
            }
            _torchState.value = enabled
        }
    }

    init {
        broadcastFactory.create(ACTION_TOGGLE_TORCH)
            .onEach { toggleTorch() }
            .launchIn(scope)
    }

    suspend fun toggleTorch() = withContext(dispatchers.main) {
        tryOrToast {
            cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
                override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                    tryOrToast {
                        cameraManager.unregisterTorchCallback(this)
                        cameraManager.setTorchMode(cameraId, !enabled)
                        stateActor.offer(!enabled)
                    }
                }

                override fun onTorchModeUnavailable(cameraId: String) {
                    tryOrToast {
                        cameraManager.unregisterTorchCallback(this)
                        toaster.toast(R.string.es_failed_to_toggle_torch)
                        stateActor.offer(false)
                    }
                }
            }, null)
        }
    }

    private inline fun tryOrToast(action: () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.toast(R.string.es_failed_to_toggle_torch)
        }
    }

    companion object {
        const val ACTION_TOGGLE_TORCH = "com.ivianuu.essentials.torch.TOGGLE_TORCH"
    }
}

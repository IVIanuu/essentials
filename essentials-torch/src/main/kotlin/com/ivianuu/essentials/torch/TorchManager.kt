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
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.essentials.util.globalScope
import com.ivianuu.injekt.ApplicationScoped
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/**
 * Provides the torch state
 */
@Given(ApplicationScoped::class)
class TorchManager {

    private val _torchState = MutableStateFlow(false)
    val torchState: StateFlow<Boolean> get() = _torchState

    private var foregroundJob: ForegroundJob? = null

    private val stateActor = globalScope.actor<Boolean> {
        for (enabled in this) {
            d { "update state $enabled" }
            foregroundJob = if (enabled) {
                given<ForegroundManager>().startJob(createTorchNotification())
            } else {
                // todo use foregroundJob?.stop() once compiler is fixed
                if (foregroundJob != null) foregroundJob!!.stop()
                null
            }
            _torchState.value = enabled
        }
    }

    init {
        BroadcastFactory.create(ACTION_TOGGLE_TORCH)
            .onEach { toggleTorch() }
            .launchIn(globalScope)
    }

    suspend fun toggleTorch() = withContext(dispatchers.main) {
        tryOrToast {
            given<CameraManager>().registerTorchCallback(object : CameraManager.TorchCallback() {
                override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                    tryOrToast {
                        given<CameraManager>().unregisterTorchCallback(this)
                        given<CameraManager>().setTorchMode(cameraId, !enabled)
                        stateActor.offer(!enabled)
                    }
                }

                override fun onTorchModeUnavailable(cameraId: String) {
                    tryOrToast {
                        given<CameraManager>().unregisterTorchCallback(this)
                        Toaster.toast(R.string.es_failed_to_toggle_torch)
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
            Toaster.toast(R.string.es_failed_to_toggle_torch)
        }
    }

    companion object {
        const val ACTION_TOGGLE_TORCH = "com.ivianuu.essentials.torch.TOGGLE_TORCH"
    }
}

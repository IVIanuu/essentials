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

package com.ivianuu.essentials.gestures.torch

import android.hardware.camera2.CameraManager
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Provides the torch state
 */
@ApplicationScope
@Single
class TorchManager internal constructor(
    broadcastFactory: BroadcastFactory,
    private val cameraManager: CameraManager,
    private val dispatchers: AppDispatchers,
    private val foregroundManager: ForegroundManager,
    private val foregroundComponent: TorchForegroundComponent,
    private val toaster: Toaster
) {

    private val _torchState = ConflatedBroadcastChannel(false)
    val torchState: Flow<Boolean>
        get() = _torchState.asFlow()

    init {
        broadcastFactory.create(ACTION_TOGGLE_TORCH)
            .onEach { toggleTorch() }
            .flowOn(dispatchers.main)
            .launchIn(GlobalScope)
    }

    fun toggleTorch() {
        tryOrToast {
            cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
                override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                    tryOrToast {
                        cameraManager.unregisterTorchCallback(this)
                        cameraManager.setTorchMode(cameraId, !enabled)
                        updateState(!enabled)
                    }
                }

                override fun onTorchModeUnavailable(cameraId: String) {
                    tryOrToast {
                        cameraManager.unregisterTorchCallback(this)
                        toaster.toast(R.string.es_failed_to_toggle_torch)
                        updateState(false)
                    }
                }
            }, null)
        }
    }

    private fun tryOrToast(action: () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.toast(R.string.es_failed_to_toggle_torch)
        }
    }

    private fun updateState(enabled: Boolean) {
        d { "update state $enabled" }
        if (enabled) {
            foregroundManager.startForeground(foregroundComponent)
        } else {
            foregroundManager.stopForeground(foregroundComponent)
        }
        _torchState.offer(enabled)
    }

    companion object {
        const val ACTION_TOGGLE_TORCH = "com.ivianuu.essentials.gestures.torch.TOGGLE_TORCH"
    }
}

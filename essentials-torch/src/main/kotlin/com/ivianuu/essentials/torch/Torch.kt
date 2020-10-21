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

package com.ivianuu.essentials.torch

import android.hardware.camera2.CameraManager
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.broadcast.broadcasts
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.coroutines.offerSafe
import com.ivianuu.essentials.foreground.ForegroundJob
import com.ivianuu.essentials.foreground.startForegroundJob
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.showToastRes
import com.ivianuu.injekt.ImplBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

interface Torch {
    val torchState: StateFlow<Boolean>

    suspend fun setTorchState(newState: Boolean)

    companion object {
        const val ACTION_TOGGLE_TORCH = "com.ivianuu.essentials.torch.TOGGLE_TORCH"
    }
}

suspend fun Torch.toggle() {
    setTorchState(!torchState.value)
}

@ImplBinding(ApplicationComponent::class)
class TorchImpl(
    broadcasts: broadcasts,
    private val cameraManager: CameraManager,
    private val createTorchNotification: createTorchNotification,
    globalScope: GlobalScope,
    private val logger: Logger,
    private val mainDispatcher: MainDispatcher,
    private val showToastRes: showToastRes,
    private val startForegroundJob: startForegroundJob
) : Torch {

    private val _torchState = MutableStateFlow(false)
    override val torchState: StateFlow<Boolean> get() = _torchState

    private var foregroundJob: ForegroundJob? = null

    private val stateActor = globalScope.actor<Boolean> {
        for (enabled in this) {
            logger.d("update state $enabled")
            foregroundJob = if (enabled) {
                startForegroundJob(createTorchNotification())
            } else {
                // todo use foregroundJob?.stop() once compiler is fixed
                if (foregroundJob != null) foregroundJob!!.stop()
                null
            }
            _torchState.value = enabled
        }
    }

    init {
        broadcasts(Torch.ACTION_TOGGLE_TORCH)
            .onEach { toggle() }
            .launchIn(globalScope)
    }

    override suspend fun setTorchState(newState: Boolean) {
        runCatching {
            withContext(mainDispatcher) {
                suspendCancellableCoroutine<Boolean> { continuation ->
                    cameraManager.registerTorchCallback(
                        object : CameraManager.TorchCallback() {
                            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                                cameraManager.unregisterTorchCallback(this)
                                cameraManager.setTorchMode(cameraId, newState)
                                stateActor.offerSafe(newState)
                                continuation.resume(newState)
                            }

                            override fun onTorchModeUnavailable(cameraId: String) {
                                cameraManager.unregisterTorchCallback(this)
                                showToastRes(R.string.es_failed_to_toggle_torch)
                                stateActor.offerSafe(false)
                                continuation.resume(false)
                            }
                        },
                        null
                    )
                }
            }
        }.onFailure {
            it.printStackTrace()
            showToastRes(R.string.es_failed_to_toggle_torch)
        }
    }
}

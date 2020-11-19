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
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.torch.TorchAction.UpdateTorchEnabled
import com.ivianuu.essentials.ui.store.StateEffect
import com.ivianuu.essentials.util.showToastRes
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

@StateEffect
@FunBinding
suspend fun updateAndroidTorchState(
    cameraManager: CameraManager,
    mainDispatcher: MainDispatcher,
    showToastRes: showToastRes,
    dispatchTorchAction: DispatchAction<TorchAction>,
    @FunApi torchState: TorchState
) {
    runCatching {
        withContext(mainDispatcher) {
            suspendCancellableCoroutine<Unit> { continuation ->
                var resumed = false
                fun resumeIfNeeded() {
                    if (resumed) return
                    resumed = true
                    continuation.resume(Unit)
                }
                val callback = object : CameraManager.TorchCallback() {
                    override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                        cameraManager.unregisterTorchCallback(this)
                        cameraManager.setTorchMode(cameraId, torchState.torchEnabled)
                        resumeIfNeeded()
                    }

                    override fun onTorchModeUnavailable(cameraId: String) {
                        cameraManager.unregisterTorchCallback(this)
                        showToastRes(R.string.es_failed_to_toggle_torch)
                        resumeIfNeeded()
                    }
                }
                cameraManager.registerTorchCallback(callback, null)
                continuation.invokeOnCancellation {
                    cameraManager.unregisterTorchCallback(callback)
                }
            }
        }
    }.onFailure {
        it.printStackTrace()
        showToastRes(R.string.es_failed_to_toggle_torch)
        dispatchTorchAction(UpdateTorchEnabled(false))
    }
}

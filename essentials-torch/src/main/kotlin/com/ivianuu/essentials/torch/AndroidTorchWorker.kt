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
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.torch.TorchAction.UpdateTorchEnabled
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.component.AppComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Given
fun androidTorchWorker(
    @Given cameraManager: CameraManager,
    @Given dispatch: Collector<TorchAction>,
    @Given state: Flow<TorchState>,
    @Given toaster: Toaster
): ScopeWorker<AppComponent> = {
    state
        .onEach { currentState ->
            val cameraId = cameraManager.cameraIdList[0]
            runKatching {
                cameraManager.setTorchMode(cameraId, currentState.torchEnabled)
            }.onFailure {
                it.printStackTrace()
                toaster.showToast(R.string.es_failed_to_toggle_torch)
                dispatch(UpdateTorchEnabled(false))
            }
        }
        .collect()
}

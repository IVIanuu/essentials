/*
 * Copyright 2021 Manuel Wrage
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
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.scope.AppScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update

@Provide fun androidTorchWorker(
  cameraManager: @SystemService CameraManager,
  torchStore: MutableStateFlow<TorchState>,
  rp: ResourceProvider,
  toaster: Toaster
): ScopeWorker<AppScope> = {
  var wasEverEnabled = false
  torchStore.collect { torchState ->
    catch {
      val cameraId = cameraManager.cameraIdList[0]
      cameraManager.setTorchMode(cameraId, torchState)
      wasEverEnabled = wasEverEnabled || torchState
    }.onFailure {
      torchStore.update { false }
      it.printStackTrace()
      if (wasEverEnabled)
        showToast(R.string.es_failed_to_toggle_torch)
    }
  }
}

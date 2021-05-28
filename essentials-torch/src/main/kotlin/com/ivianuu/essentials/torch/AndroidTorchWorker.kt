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

import android.hardware.camera2.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

@Provide fun androidTorchWorker(
  cameraManager: @SystemService CameraManager,
  torchStore: MutableStateFlow<TorchState>,
  rp: ResourceProvider,
  toaster: Toaster
): ScopeWorker<AppScope> = {
  torchStore.collect { torchState ->
    catch {
      val cameraId = cameraManager.cameraIdList[0]
      cameraManager.setTorchMode(cameraId, torchState)
    }.onFailure {
      it.printStackTrace()
      showToast(R.string.es_failed_to_toggle_torch)
      torchStore.update { false }
    }
  }
}

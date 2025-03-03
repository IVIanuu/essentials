/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.hardware.camera2.*
import android.hardware.camera2.CameraManager.*
import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

@Provide object FlashlightActionId : ActionId("flashlight") {
  @Provide fun action(
    cameraManager: @SystemService CameraManager,
    coroutineContexts: CoroutineContexts
  ) = Action(
    id = FlashlightActionId,
    title = "Flashlight",
    icon = {
      Icon(
        if (
          cameraManager.flashlightState(coroutineContexts)
            .collectAsState(false).value) Icons.Default.FlashlightOn
        else Icons.Default.FlashlightOff,
        null
      )
    }
  )

  @Provide fun executor(
    cameraManager: @SystemService CameraManager,
    coroutineContexts: CoroutineContexts,
    toaster: Toaster
  ) = ActionExecutor<FlashlightActionId> {
    withContext(coroutineContexts.main) {
      val state = cameraManager.flashlightState(coroutineContexts).first()
      catch {
        val cameraId = cameraManager.cameraIdList[0]
        cameraManager.setTorchMode(cameraId, !state)
      }.onLeft {
        it.printStackTrace()
        toaster.toast("Failed to enable flashlight!")
      }
    }
  }

  private fun CameraManager.flashlightState(coroutineContexts: CoroutineContexts) = callbackFlow {
    val rearCameraId = cameraIdList[0]
    val callback = object : TorchCallback() {
      override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
        if (rearCameraId == cameraId)
          trySend(enabled)
      }
    }
    registerTorchCallback(callback ,null)
    awaitClose { unregisterTorchCallback(callback) }
  }.flowOn(coroutineContexts.main)
}

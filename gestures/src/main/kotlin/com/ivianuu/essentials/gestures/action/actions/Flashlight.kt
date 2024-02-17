/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraManager.TorchCallback
import androidx.compose.material.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.asLog
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

@Provide object FlashlightActionId : ActionId("flashlight")

@Provide fun flashlightAction(
  cameraManager: @SystemService CameraManager,
  coroutineContexts: CoroutineContexts,
  resources: Resources
) = Action(
  id = FlashlightActionId,
  title = resources(R.string.action_torch),
  icon = {
    Icon(
      painterResource(
        if (remember {
          cameraManager.flashlightState(coroutineContexts)
        }.collectAsState(false).value) R.drawable.ic_flashlight_on
        else R.drawable.ic_flashlight_off
      ),
      null
    )
  }
)

@Provide fun flashlightActionExecutor(
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
      toaster(R.string.failed_to_enable_torch)
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

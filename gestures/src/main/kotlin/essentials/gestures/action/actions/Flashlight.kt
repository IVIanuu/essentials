/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.hardware.camera2.*
import android.hardware.camera2.CameraManager.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.github.michaelbull.result.onFailure
import essentials.*
import essentials.coroutines.*
import essentials.gestures.action.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

@Provide object FlashlightActionId : ActionId("flashlight") {
  @Provide fun action(scope: Scope<*> = inject) = Action(
    id = FlashlightActionId,
    title = "Flashlight",
    icon = {
      Icon(
        if (
          flashlightState()
            .collectAsState(false).value) Icons.Default.FlashlightOn
        else Icons.Default.FlashlightOff,
        null
      )
    }
  )

  @Provide suspend fun execute(scope: Scope<*> = inject): ActionExecutorResult<FlashlightActionId> {
    withContext(coroutineContexts().main) {
      val state = flashlightState().first()
      catch {
        val cameraManager = systemService<CameraManager>()
        val cameraId = cameraManager.cameraIdList[0]
        cameraManager.setTorchMode(cameraId, !state)
      }.onFailure {
        it.printStackTrace()
        showToast("Failed to enable flashlight!")
      }
    }
  }

  private fun flashlightState(scope: Scope<*> = inject) = callbackFlow {
    val cameraManager = systemService<CameraManager>()
    val rearCameraId = cameraManager.cameraIdList[0]
    val callback = object : TorchCallback() {
      override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
        if (rearCameraId == cameraId)
          trySend(enabled)
      }
    }
    cameraManager.registerTorchCallback(callback ,null)
    awaitClose { cameraManager.unregisterTorchCallback(callback) }
  }.flowOn(coroutineContexts().main)
}

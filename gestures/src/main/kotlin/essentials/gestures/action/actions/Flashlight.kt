/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.app.*
import android.hardware.camera2.*
import android.hardware.camera2.CameraManager.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.content.*
import com.github.michaelbull.result.*
import essentials.*
import essentials.coroutines.*
import essentials.gestures.action.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

@Provide object FlashlightActionId : ActionId("flashlight") {
  @Provide fun action(
    context: Application = inject,
    coroutineContexts: CoroutineContexts = inject
  ) = Action(
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

  @Provide suspend fun execute(
    context: Application = inject,
    coroutineContexts: CoroutineContexts = inject,
    showToast: showToast
  ): ActionExecutorResult<FlashlightActionId> {
    withContext(coroutineContexts.main) {
      val state = flashlightState().first()
      catch {
        val cameraId = context.getSystemService<CameraManager>()!!.cameraIdList[0]
        context.getSystemService<CameraManager>()!!.setTorchMode(cameraId, !state)
      }.onFailure {
        it.printStackTrace()
        showToast("Failed to enable flashlight!")
      }
    }
  }

  private fun flashlightState(
    context: Application = inject,
    coroutineContexts: CoroutineContexts = inject
  ) = callbackFlow {
    val cameraManager = context.getSystemService<CameraManager>()!!
    val rearCameraId = cameraManager.cameraIdList[0]
    val callback = object : TorchCallback() {
      override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
        if (rearCameraId == cameraId)
          trySend(enabled)
      }
    }
    cameraManager.registerTorchCallback(callback ,null)
    awaitClose { cameraManager.unregisterTorchCallback(callback) }
  }.flowOn(coroutineContexts.main)
}

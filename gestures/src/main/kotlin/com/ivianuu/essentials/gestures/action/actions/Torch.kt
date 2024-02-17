/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.hardware.camera2.CameraManager
import androidx.compose.material.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.compose.compositionStateFlow
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
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

@Provide object TorchActionId : ActionId("torch")

@Provide fun torchAction(resources: Resources, torchManager: TorchManager) = Action(
  id = TorchActionId,
  title = resources(R.string.action_torch),
  icon = {
    Icon(
      painterResource(
        if (torchManager.torchEnabled.collectAsState().value) R.drawable.ic_flashlight_on
        else R.drawable.ic_flashlight_off
      ),
      null
    )
  }
)

@Provide fun torchActionExecutor(torchManager: TorchManager) = ActionExecutor<TorchActionId>
{ torchManager.updateTorchState(!torchManager.torchEnabled.value) }

@Provide @Scoped<AppScope> class TorchManager(
  private val cameraManager: @SystemService CameraManager,
  private val logger: Logger,
  scope: ScopedCoroutineScope<AppScope>,
  private val toaster: Toaster
) {
  private var _torchEnabled by mutableStateOf(false)
  val torchEnabled = scope.compositionStateFlow {
    if (!_torchEnabled) return@compositionStateFlow _torchEnabled

    LaunchedEffect(true) {
      catch {
        val cameraId = cameraManager.cameraIdList[0]
        logger.log { "enable torch" }
        cameraManager.setTorchMode(cameraId, true)
        onCancel {
          logger.log { "disable torch on cancel" }
          catch { cameraManager.setTorchMode(cameraId, false) }
          _torchEnabled = false
        }
      }.onLeft {
        logger.log(priority = Logger.Priority.ERROR) { "Failed to enable torch ${it.asLog()}" }
        toaster(R.string.failed_to_enable_torch)
        _torchEnabled = false
      }
    }

    _torchEnabled
  }

  suspend fun updateTorchState(value: Boolean) {
    _torchEnabled = value
  }
}

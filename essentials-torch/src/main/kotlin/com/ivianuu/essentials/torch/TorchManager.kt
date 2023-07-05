/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.torch

import android.hardware.camera2.CameraManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.compositionStateFlow
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.asLog
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.util.Notification
import com.ivianuu.essentials.util.NotificationModel
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

interface TorchManager {
  val torchEnabled: StateFlow<Boolean>

  suspend fun updateTorchState(value: Boolean)
}

@Provide @Scoped<AppScope> class TorchManagerImpl(
  private val cameraManager: @SystemService CameraManager,
  private val foregroundManager: ForegroundManager,
  private val logger: Logger,
  scope: ScopedCoroutineScope<AppScope>,
  private val toaster: Toaster
) : TorchManager {
  private var _torchEnabled by mutableStateOf(false)
  override val torchEnabled = scope.compositionStateFlow {
    if (!_torchEnabled) return@compositionStateFlow _torchEnabled

    LaunchedEffect(true) {
      foregroundManager.startForeground(TorchNotification)
    }

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
      }.onFailure {
        logger.log(priority = Logger.Priority.ERROR) { "Failed to enable torch ${it.asLog()}" }
        toaster(R.string.es_failed_to_enable_torch)
        _torchEnabled = false
      }
    }

    _torchEnabled
  }

  override suspend fun updateTorchState(value: Boolean) {
    _torchEnabled = value
  }
}

@Serializable object TorchNotification : Notification(channelId = "torch", importance = Importance.LOW)

@Provide fun torchNotificationModel(resources: Resources, torchManager: TorchManager) = Model {
  NotificationModel<TorchNotification>(
    icon = resources(R.drawable.es_ic_flashlight_on),
    title = resources(R.string.es_notif_title_torch),
    text = resources(R.string.es_notif_text_torch),
    onClick = action { torchManager.updateTorchState(false) }
  )
}

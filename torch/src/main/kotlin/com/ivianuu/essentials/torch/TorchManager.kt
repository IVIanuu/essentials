/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.torch

import android.app.NotificationManager
import android.hardware.camera2.CameraManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.compose.compositionStateFlow
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.asLog
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.torch.TorchManagerImpl.DisableTorchAction
import com.ivianuu.essentials.util.NotificationFactory
import com.ivianuu.essentials.util.RemoteAction
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.remoteActionOf
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json

interface TorchManager {
  val torchEnabled: StateFlow<Boolean>

  suspend fun updateTorchState(value: Boolean)
}

context(Json, Logger) @Provide @Scoped<AppScope> class TorchManagerImpl(
  private val cameraManager: @SystemService CameraManager,
  private val foregroundManager: ForegroundManager,
  private val notificationFactory: NotificationFactory,
  private val resources: Resources,
  scope: ScopedCoroutineScope<AppScope>,
  private val toaster: Toaster
) : TorchManager {
  private var _torchEnabled by mutableStateOf(false)
  override val torchEnabled = scope.compositionStateFlow {
    if (!_torchEnabled) return@compositionStateFlow _torchEnabled

    LaunchedEffect(true) {
      foregroundManager.startForeground {
        notificationFactory(
          "torch",
          resources.resource(R.string.es_notif_channel_torch),
          NotificationManager.IMPORTANCE_LOW
        ) {
          setSmallIcon(R.drawable.es_ic_flashlight_on)
          setContentTitle(resources.resource(R.string.es_notif_title_torch))
          setContentText(resources.resource(R.string.es_notif_text_torch))
          setContentIntent(remoteActionOf<DisableTorchAction>())
        }
      }
    }

    LaunchedEffect(true) {
      catch {
        val cameraId = cameraManager.cameraIdList[0]
        log { "enable torch" }
        cameraManager.setTorchMode(cameraId, true)
        onCancel {
          log { "disable torch on cancel" }
          catch { cameraManager.setTorchMode(cameraId, false) }
          _torchEnabled = false
        }
      }.onFailure {
        log(priority = Logger.Priority.ERROR) { "Failed to enable torch ${it.asLog()}" }
        toaster.toast(R.string.es_failed_to_enable_torch)
        _torchEnabled = false
      }
    }

    _torchEnabled
  }

  override suspend fun updateTorchState(value: Boolean) {
    _torchEnabled = value
  }

  fun interface DisableTorchAction : RemoteAction<Any?>

  @Provide fun disableTorchAction() = DisableTorchAction { _torchEnabled = false }
}

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.torch

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.hardware.camera2.CameraManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.compose.compositionStateFlow
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.asLog
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.essentials.util.NotificationFactory
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.context
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

interface TorchManager {
  val torchEnabled: StateFlow<Boolean>

  suspend fun updateTorchState(value: Boolean)
}

@Provide @Scoped<AppScope> class TorchManagerImpl(
  private val broadcastsFactory: BroadcastsFactory,
  private val cameraManager: @SystemService CameraManager,
  private val foregroundManager: ForegroundManager,
  private val logger: Logger,
  private val notificationFactory: NotificationFactory,
  private val resources: Resources,
  scope: ScopedCoroutineScope<AppScope>,
  private val toaster: Toaster
) : TorchManager {
  private var _torchEnabled by mutableStateOf(false)
  override val torchEnabled = scope.compositionStateFlow {
    if (!_torchEnabled) return@compositionStateFlow _torchEnabled

    foregroundManager.Foreground { createTorchNotification() }

    LaunchedEffect(true) {
      broadcastsFactory(ACTION_DISABLE_TORCH).first()
      _torchEnabled = false
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

  @SuppressLint("LaunchActivityFromNotification")
  private fun createTorchNotification(): Notification = notificationFactory(
    "torch",
    resources(R.string.es_notif_channel_torch),
    NotificationManager.IMPORTANCE_LOW
  ) {
    setAutoCancel(true)
    setSmallIcon(R.drawable.es_ic_flashlight_on)
    setContentTitle(resources(R.string.es_notif_title_torch))
    setContentText(resources(R.string.es_notif_text_torch))
    setContentIntent(
      PendingIntent.getBroadcast(
        context,
        87,
        Intent(ACTION_DISABLE_TORCH),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )
    )
  }

  private companion object {
    private const val ACTION_DISABLE_TORCH = "com.ivianuu.essentials.torch.DISABLE_TORCH"
  }
}

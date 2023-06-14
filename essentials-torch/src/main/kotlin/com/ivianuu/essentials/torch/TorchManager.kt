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
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.coroutines.race
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.asLog
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.essentials.util.NotificationFactory
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.context
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface TorchManager {
  val torchEnabled: StateFlow<Boolean>

  suspend fun setTorchState(value: Boolean)
}

@Provide @Scoped<AppScope> class TorchManagerImpl(
  private val broadcastsFactory: BroadcastsFactory,
  private val cameraManager: @SystemService CameraManager,
  private val foregroundManager: ForegroundManager,
  private val logger: Logger,
  private val notificationFactory: NotificationFactory,
  private val resources: Resources,
  private val scope: ScopedCoroutineScope<AppScope>,
  private val toaster: Toaster
) : TorchManager {
  private val _torchEnabled = MutableStateFlow(false)
  override val torchEnabled: StateFlow<Boolean> by this::_torchEnabled

  private val torchJobLock = Mutex()
  private var torchJob: Job? = null

  override suspend fun setTorchState(value: Boolean) {
    torchJobLock.withLock {
      torchJob?.cancel()
      torchJob = null
      torchJob = scope.launch { doSetTorchState(value) }
    }
  }

  private suspend fun doSetTorchState(value: Boolean) {
    logger.log { "handle torch state $value" }
    if (!value) {
      _torchEnabled.value = false
      return
    }

    foregroundManager.runInForeground(createTorchNotification()) {
      race(
        { enableTorch() },
        { broadcastsFactory(ACTION_DISABLE_TORCH).first() }
      )
    }
  }

  private suspend fun enableTorch() {
    catch {
      val cameraId = cameraManager.cameraIdList[0]
      logger.log { "enable torch" }
      cameraManager.setTorchMode(cameraId, true)
      _torchEnabled.value = true

      // todo remove dummy block param once fixed
      onCancel(block = { awaitCancellation() }) {
        logger.log { "disable torch on cancel" }
        catch { cameraManager.setTorchMode(cameraId, false) }
        _torchEnabled.value = false
      }
    }.onFailure {
      logger.log(priority = Logger.Priority.ERROR) { "Failed to enable torch ${it.asLog()}" }
      toaster(R.string.es_failed_to_enable_torch)
      setTorchState(false)
    }
  }

  @SuppressLint("LaunchActivityFromNotification")
  private fun createTorchNotification(): Notification = notificationFactory(
    NOTIFICATION_CHANNEL_ID,
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
    private const val NOTIFICATION_CHANNEL_ID = "torch"
    private const val ACTION_DISABLE_TORCH = "com.ivianuu.essentials.torch.DISABLE_TORCH"
  }
}

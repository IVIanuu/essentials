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
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.coroutines.race
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.asLog
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.essentials.util.NotificationFactory
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.context
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
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

context(
BroadcastsFactory,
CameraManager,
ForegroundManager,
Logger,
NamedCoroutineScope<AppScope>,
NotificationFactory,
ToastContext
)
@Provide @Scoped<AppScope> class TorchManagerImpl : TorchManager {
  private val _torchEnabled = MutableStateFlow(false)
  override val torchEnabled: StateFlow<Boolean> by this::_torchEnabled

  private val torchJobLock = Mutex()
  private var torchJob: Job? = null

  override suspend fun setTorchState(value: Boolean) {
    torchJobLock.withLock {
      torchJob?.cancel()
      torchJob = null
      torchJob = launch { doSetTorchState(value) }
    }
  }

  private suspend fun doSetTorchState(value: Boolean) {
    log { "handle torch state $value" }
    if (!value) {
      _torchEnabled.value = false
      return
    }

    runInForeground(createTorchNotification()) {
      race(
        { enableTorch() },
        { broadcasts(ACTION_DISABLE_TORCH).first() }
      )
    }
  }

  private suspend fun enableTorch() {
    catch {
      val cameraId = cameraIdList[0]
      log { "enable torch" }
      setTorchMode(cameraId, true)
      _torchEnabled.value = true

      // todo remove dummy block param once fixed
      onCancel(block = { awaitCancellation() }) {
        log { "disable torch on cancel" }
        catch { setTorchMode(cameraId, false) }
        _torchEnabled.value = false
      }
    }.onFailure {
      log(Logger.Priority.ERROR) { "Failed to enable torch ${it.asLog()}" }
      showToast(R.string.es_failed_to_enable_torch)
      setTorchState(false)
    }
  }

  @SuppressLint("LaunchActivityFromNotification")
  private fun createTorchNotification(): Notification = buildNotification(
    NOTIFICATION_CHANNEL_ID,
    loadResource(R.string.es_notif_channel_torch),
    NotificationManager.IMPORTANCE_LOW
  ) {
    setAutoCancel(true)
    setSmallIcon(R.drawable.es_ic_flashlight_on)
    setContentTitle(loadResource(R.string.es_notif_title_torch))
    setContentText(loadResource(R.string.es_notif_text_torch))
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

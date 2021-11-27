package com.ivianuu.essentials.torch

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.hardware.camera2.CameraManager
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.coroutines.race
import com.ivianuu.essentials.foreground.ForegroundManager
import com.ivianuu.essentials.foreground.startForeground
import com.ivianuu.essentials.loadResource
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
import com.ivianuu.injekt.coroutines.MainDispatcher
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

interface Torch {
  val torchEnabled: StateFlow<Boolean>

  suspend fun setTorchState(value: Boolean)
}

@Provide @Scoped<AppScope> class TorchImpl(
  private val broadcastsFactory: BroadcastsFactory,
  private val cameraManager: @SystemService CameraManager,
  private val foregroundManager: ForegroundManager,
  private val mainDispatcher: MainDispatcher,
  private val notificationFactory: NotificationFactory,
  private val S: NamedCoroutineScope<AppScope>,
  private val L: Logger,
  private val T: ToastContext
) : Torch {
  private val _torchEnabled = MutableStateFlow(false)
  override val torchEnabled: StateFlow<Boolean> by this::_torchEnabled

  private val torchJobLock = Mutex()
  private var torchJob: Job? = null

  override suspend fun setTorchState(value: Boolean) {
    torchJobLock.withLock {
      torchJob?.cancel()
      torchJob = null
      torchJob = launch { handleTorchState(value) }
    }
  }

  private suspend fun handleTorchState(value: Boolean) {
    log { "handle torch state $value" }
    if (!value) {
      _torchEnabled.value = false
      return
    }

    race(
      { enableTorch() },
      { broadcastsFactory(ACTION_DISABLE_TORCH).first() },
      { foregroundManager.startForeground(64578, createTorchNotification()) }
    )
  }

  private suspend fun enableTorch() = catch {
    val cameraId = cameraManager.cameraIdList[0]
    log { "enable torch" }
    cameraManager.setTorchMode(cameraId, true)
    _torchEnabled.value = true

    onCancel(
      block = {
        withContext(mainDispatcher) {
          suspendCancellableCoroutine<Unit> { cont ->
            val callback = object : CameraManager.TorchCallback() {
              override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                super.onTorchModeChanged(cameraId, enabled)
                if (!enabled) {
                  cameraManager.unregisterTorchCallback(this)
                  if (cont.isActive) cont.resume(Unit)
                }
              }
              override fun onTorchModeUnavailable(cameraId: String) {
                super.onTorchModeUnavailable(cameraId)
                cameraManager.unregisterTorchCallback(this)
                if (cont.isActive) cont.resume(Unit)
              }
            }

            cont.invokeOnCancellation {
              cameraManager.unregisterTorchCallback(callback)
            }

            cameraManager.registerTorchCallback(callback, null)
          }
        }

        log { "torch unavailable" }
        catch { cameraManager.setTorchMode(cameraId, false) }
        _torchEnabled.value = false
      },
      onCancel = {
        log { "disable torch on cancel" }
        catch { cameraManager.setTorchMode(cameraId, false) }
        _torchEnabled.value = false
      }
    )
  }.onFailure {
    log(Logger.Priority.ERROR) { "Failed to enable torch ${it.asLog()}" }
    showToast(R.string.es_failed_to_enable_torch)
    setTorchState(false)
  }

  @SuppressLint("LaunchActivityFromNotification")
  private fun createTorchNotification(): Notification = notificationFactory.build(
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

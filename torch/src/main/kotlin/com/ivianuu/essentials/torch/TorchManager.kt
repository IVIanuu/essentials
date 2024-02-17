/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.torch

import android.hardware.camera2.CameraManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import arrow.core.Either
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.compose.compositionStateFlow
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.asLog
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide

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

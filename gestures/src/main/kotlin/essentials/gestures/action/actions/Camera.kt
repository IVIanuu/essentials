/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.content.*
import android.content.pm.*
import android.hardware.camera2.*
import android.os.*
import android.provider.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import essentials.*
import essentials.accessibility.*
import essentials.compose.*
import essentials.gestures.action.*
import essentials.logging.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

@Provide object CameraActionId : ActionId("camera") {
  @Provide val action get() = Action(
    id = CameraActionId,
    title = "Camera",
    icon = { Icon(Icons.Default.PhotoCamera, null) },
    permissions = listOf(
      ActionAccessibilityPermission::class,
      ActionSystemOverlayPermission::class
    ),
    turnScreenOn = true,
    closeSystemDialogs = true
  )

  @Provide suspend fun execute(
    appScope: Scope<AppScope>,
    cameraManager: @SystemService CameraManager,
    currentAppProducer: @Composable () -> CurrentApp?,
    screenStateProducer: @Composable () -> ScreenState,
    logger: Logger,
    packageManager: PackageManager,
    sendIntent: sendActionIntent
  ): ActionExecutorResult<CameraActionId> {
    val cameraApp = packageManager.resolveActivity(
      Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE),
      PackageManager.MATCH_DEFAULT_ONLY
    )!!

    val intent = if (cameraApp.activityInfo!!.packageName == "com.motorola.camera2")
      packageManager.getLaunchIntentForPackage("com.motorola.camera2")!!
    else Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE)

    val frontCamera = cameraManager.cameraIdList
      .firstOrNull {
        cameraManager.getCameraCharacteristics(it)[CameraCharacteristics.LENS_FACING] ==
            CameraCharacteristics.LENS_FACING_FRONT
      }

    val currentScreenState = moleculeFlow { screenStateProducer() }.first()

    val frontFacing = if (frontCamera != null &&
      currentScreenState != ScreenState.OFF &&
      (currentScreenState == ScreenState.UNLOCKED ||
          appScope.scopeOfOrNull<AccessibilityScope>()
            ?.accessibilityService?.rootInActiveWindow?.packageName != "com.android.systemui") &&
      cameraApp.activityInfo!!.packageName == moleculeFlow { currentAppProducer() }.first()
    )
      suspendCancellableCoroutine<Boolean> { cont ->
        cameraManager.registerAvailabilityCallback(object : CameraManager.AvailabilityCallback() {
          override fun onCameraAvailable(cameraId: String) {
            super.onCameraAvailable(cameraId)
            cameraManager.unregisterAvailabilityCallback(this)
            if (cameraId == frontCamera)
              catch { cont.resume(true) }
          }

          override fun onCameraUnavailable(cameraId: String) {
            super.onCameraUnavailable(cameraId)
            cameraManager.unregisterAvailabilityCallback(this)
            if (cameraId == frontCamera)
              catch { cont.resume(false) }
          }
        }, Handler(Looper.getMainLooper()))
      }
    else null

    logger.d { "open camera with $frontFacing" }

    if (frontFacing != null)
      intent.addCameraFacingExtras(frontFacing)

    sendIntent(intent, null)
  }

  private fun Intent.addCameraFacingExtras(frontFacing: Boolean) {
    putExtra(
      "android.intent.extras.CAMERA_FACING",
      if (frontFacing) CameraCharacteristics.LENS_FACING_FRONT
      else CameraCharacteristics.LENS_FACING_BACK
    )

    putExtra("android.intent.extras.LENS_FACING_FRONT", if (frontFacing) 1 else 0)
    putExtra("android.intent.extra.USE_FRONT_CAMERA", frontFacing)
    putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", frontFacing)

    // one plus
    putExtra(
      "com.android.systemui.camera_launch_source_gesture",
      if (frontFacing) 512 else 256
    )
  }
}

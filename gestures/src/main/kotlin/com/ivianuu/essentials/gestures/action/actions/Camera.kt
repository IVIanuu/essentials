/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import arrow.core.Either
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.ScopeManager
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.accessibility.AccessibilityScope
import com.ivianuu.essentials.accessibility.accessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionAccessibilityPermission
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionSystemOverlayPermission
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.scopeOfOrNull
import com.ivianuu.essentials.util.ScreenState
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Provide object CameraActionId : ActionId("camera")

@Provide fun cameraAction(resources: Resources) = Action(
  id = CameraActionId,
  title = resources(R.string.es_action_camera),
  icon = staticActionIcon(R.drawable.es_ic_photo_camera),
  permissions = listOf(
    typeKeyOf<ActionAccessibilityPermission>(),
    typeKeyOf<ActionSystemOverlayPermission>()
  ),
  turnScreenOn = true,
  closeSystemDialogs = true
)

@Provide fun cameraActionExecutor(
  actionIntentSender: ActionIntentSender,
  cameraManager: @SystemService CameraManager,
  currentApp: Flow<CurrentApp?>,
  logger: Logger,
  packageManager: PackageManager,
  scopeManager: ScopeManager,
  screenState: Flow<ScreenState>,
) = ActionExecutor<CameraActionId> {
  val cameraApp = packageManager
    .resolveActivity(
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

  val currentScreenState = screenState.first()

  val frontFacing = if (frontCamera != null &&
    currentScreenState != ScreenState.OFF &&
    (currentScreenState == ScreenState.UNLOCKED ||
        scopeManager.scopeOfOrNull<AccessibilityScope>().first()
          ?.accessibilityService?.rootInActiveWindow?.packageName != "com.android.systemui") &&
    cameraApp.activityInfo!!.packageName == currentApp.first()?.value
  )
    suspendCancellableCoroutine<Boolean> { cont ->
      cameraManager.registerAvailabilityCallback(object : CameraManager.AvailabilityCallback() {
        override fun onCameraAvailable(cameraId: String) {
          super.onCameraAvailable(cameraId)
          cameraManager.unregisterAvailabilityCallback(this)
          if (cameraId == frontCamera)
            Either.catch { cont.resume(true) }
        }

        override fun onCameraUnavailable(cameraId: String) {
          super.onCameraUnavailable(cameraId)
          cameraManager.unregisterAvailabilityCallback(this)
          if (cameraId == frontCamera)
            Either.catch { cont.resume(false) }
        }
      }, Handler(Looper.getMainLooper()))
    }
  else null

  logger.log { "open camera with $frontFacing" }

  if (frontFacing != null)
    intent.addCameraFacingExtras(frontFacing)

  actionIntentSender(intent, null)
}

fun Intent.addCameraFacingExtras(frontFacing: Boolean) {
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

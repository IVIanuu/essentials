package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.provider.MediaStore
import android.view.accessibility.AccessibilityNodeInfo
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.ScopeManager
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityScope
import com.ivianuu.essentials.accessibility.accessibilityService
import com.ivianuu.essentials.accessibility.firstNodeOrNull
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
import com.ivianuu.essentials.util.DeviceScreenManager
import com.ivianuu.essentials.util.ScreenState
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Provide object SnapchatActionId : ActionId("snapchat")

@Provide fun snapchatAction(resources: Resources) = Action(
  id = SnapchatActionId,
  title = "Snapchat",
  icon = staticActionIcon(R.drawable.ic_photo_camera),
  permissions = listOf(
    typeKeyOf<ActionAccessibilityPermission>(),
    typeKeyOf<ActionSystemOverlayPermission>()
  ),
  turnScreenOn = true,
  closeSystemDialogs = true
)

@Provide fun snapchatActionExecutor(
  actionIntentSender: ActionIntentSender,
  appContext: AppContext,
  cameraManager: @SystemService CameraManager,
  currentApp: Flow<CurrentApp?>,
  logger: Logger,
  packageManager: PackageManager,
  scopeManager: ScopeManager
) = ActionExecutor<SnapchatActionId> {
  logger.log { "current app ${currentApp.first()}" }

  if (currentApp.first()?.value != "com.snapchat.android") {
    val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE).apply {
      `package` = "com.snapchat.android"
    }
    actionIntentSender(intent, null)
  } else {
    val accessibilityService = scopeManager.scopeOfOrNull<AccessibilityScope>()
      .first()!!.accessibilityService

    val snapchatContext = appContext.createPackageContext(
      "com.snapchat.android", 0
    )

    val id = snapchatContext.resources.getIdentifier(
      "flip_camera", "string", "com.snapchat.android"
    )

    val flipCameraDesc = snapchatContext.resources.getString(id)

    accessibilityService.rootInActiveWindow
      .firstNodeOrNull { it.contentDescription == flipCameraDesc }
      ?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
  }
}

@Provide val snapchatActionAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    flags = AccessibilityServiceInfo.CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT or
        AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
  )

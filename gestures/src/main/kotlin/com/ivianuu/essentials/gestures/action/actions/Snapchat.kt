package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.*
import android.content.*
import android.content.pm.*
import android.hardware.camera2.*
import android.provider.*
import android.view.accessibility.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.recentapps.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

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

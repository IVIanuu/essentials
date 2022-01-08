/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.*
import android.view.accessibility.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide object NotificationsActionId : ActionId("notifications")

@Provide fun notificationsAction(RP: ResourceProvider) = Action(
  id = NotificationsActionId,
  title = loadResource(R.string.es_action_notifications),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(Icons.Default.Notifications)
)

@Provide fun notificationsActionExecutor(
  closeSystemDialogs: CloseSystemDialogsUseCase,
  context: AppContext,
  globalActionExecutor: GlobalActionExecutor,
  serviceFlow: Flow<EsAccessibilityService?>
) = ActionExecutor<NotificationsActionId> {
  val targetState = catch {
    val service = serviceFlow.first()!!

    val systemUiContext = context.createPackageContext(
      "com.android.systemui", 0
    )

    val id = systemUiContext.resources.getIdentifier(
      "accessibility_desc_notification_shade", "string", "com.android.systemui")

    val notificationShadeDesc = systemUiContext.resources.getString(id)

    fun AccessibilityNodeInfo.isNotificationShadeVisibleRecursive(): Boolean {
      if (packageName != "com.android.systemui") return false
      if (paneTitle == notificationShadeDesc) return true
      return (0 until childCount)
        .any { getChild(it).isNotificationShadeVisibleRecursive() }
    }

    return@catch !service.rootInActiveWindow.isNotificationShadeVisibleRecursive()
  }.getOrElse { true }

  if (targetState)
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
  else
    closeSystemDialogs()
}

@Provide val notificationsActionAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    flags = AccessibilityServiceInfo.CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT or
        AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
  )

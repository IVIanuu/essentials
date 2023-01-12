/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.getOrElse
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Provide object NotificationsActionId : ActionId("notifications")

context(ResourceProvider) @Provide fun notificationsAction() = Action(
  id = NotificationsActionId,
  title = loadResource(R.string.es_action_notifications),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(Icons.Default.Notifications)
)

context(AppContext, CloseSystemDialogsUseCase, GlobalActionExecutor)
@Provide fun notificationsActionExecutor(
  serviceFlow: Flow<EsAccessibilityService?>
) = ActionExecutor<NotificationsActionId> {
  val targetState = catch {
    val service = serviceFlow.first()!!

    val systemUiContext = createPackageContext("com.android.systemui", 0)

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
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
  else
    closeSystemDialogs()
}

@Provide val notificationsActionAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    flags = AccessibilityServiceInfo.CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT or
        AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
  )

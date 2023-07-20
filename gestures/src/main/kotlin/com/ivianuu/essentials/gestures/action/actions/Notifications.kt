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
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityScope
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.accessibility.accessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.result.getOrElse
import com.ivianuu.essentials.scopeOfOrNull
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first

@Provide object NotificationsActionId : ActionId("notifications")

@Provide fun notificationsAction(resources: Resources) = Action(
  id = NotificationsActionId,
  title = resources(R.string.es_action_notifications),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(Icons.Default.Notifications)
)

@Provide fun notificationsActionExecutor(
  appScope: Scope<AppScope>,
  closeSystemDialogs: CloseSystemDialogsUseCase,
  context: AppContext,
  globalActionExecutor: GlobalActionExecutor
) = ActionExecutor<NotificationsActionId> {
  val targetState = catch {
    val service = appScope.scopeOfOrNull<AccessibilityScope>().first()!!.accessibilityService

    val systemUiContext = context.createPackageContext(
      "com.android.systemui", 0
    )

    val id = systemUiContext.resources.getIdentifier(
      "accessibility_desc_notification_shade", "string", "com.android.systemui"
    )

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

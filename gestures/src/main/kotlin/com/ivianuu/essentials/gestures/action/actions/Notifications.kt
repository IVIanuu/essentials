/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.*
import android.accessibilityservice.AccessibilityService.*
import android.view.accessibility.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import arrow.core.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.accessibility.AccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide object NotificationsActionId : ActionId("notifications") {
  @Provide val action get() = Action(
    id = NotificationsActionId,
    title = "Notifications",
    permissions = accessibilityActionPermissions,
    icon = staticActionIcon(Icons.Default.Notifications)
  )

  @Provide fun executor(
    accessibilityService: AccessibilityService,
    appContext: AppContext,
    closeSystemDialogs: CloseSystemDialogsUseCase,
    scopeManager: ScopeManager,
  ) = ActionExecutor<NotificationsActionId> {
    val targetState = catch {
      val service = scopeManager.scopeOfOrNull<AccessibilityScope>().first()!!.accessibilityService

      val systemUiContext = appContext.createPackageContext(
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
      accessibilityService.performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    else
      closeSystemDialogs()
  }

  @Provide val accessibilityConfig: AccessibilityConfig
    get() = AccessibilityConfig(
      flags = AccessibilityServiceInfo.CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT or
          AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
    )
}

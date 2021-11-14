/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.State
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
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Provide object NotificationsActionId : ActionId("notifications")

@Provide fun notificationsAction(RP: ResourceProvider) = Action(
  id = NotificationsActionId,
  title = loadResource(R.string.es_action_notifications),
  permissions = accessibilityActionPermissions,
  icon = singleActionIcon(Icons.Default.Notifications)
)

@Provide fun notificationsActionExecutor(
  closeSystemDialogs: CloseSystemDialogsUseCase,
  context: AppContext,
  globalActionExecutor: GlobalActionExecutor,
  service: State<EsAccessibilityService?>
): ActionExecutor<NotificationsActionId> = {
  val targetState = catch {
    val service = service.value!!

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

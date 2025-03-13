/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.accessibilityservice.*
import android.accessibilityservice.AccessibilityService.*
import android.view.accessibility.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import arrow.core.*
import essentials.*
import essentials.accessibility.*
import essentials.gestures.action.*
import injekt.*

@Provide object QuickSettingsActionId : ActionId("quick_settings") {
  @Provide val action get() = Action(
    id = QuickSettingsActionId,
    title = "Quick settings",
    permissions = accessibilityActionPermissions,
    icon = { Icon(Icons.Default.Settings, null) }
  )

  @Provide suspend fun execute(
    appScope: Scope<AppScope>,
    appContext: AppContext,
    closeSystemDialogs: closeSystemDialogs,
    performAccessibilityAction: performGlobalAccessibilityAction
  ): ActionExecutorResult<QuickSettingsActionId> {
    val targetState = catch {
      val service = appScope.scopeOfOrNull<AccessibilityScope>()!!.accessibilityService

      val systemUiContext = appContext.createPackageContext(
        "com.android.systemui", 0
      )

      val id = systemUiContext.resources.getIdentifier(
        "accessibility_desc_quick_settings", "string", "com.android.systemui"
      )

      val notificationShadeDesc = systemUiContext.resources.getString(id)

      fun AccessibilityNodeInfo.isQuickSettingsPanelVisibleRecursive(): Boolean {
        if (packageName != "com.android.systemui") return false
        if (paneTitle == notificationShadeDesc) return true
        return (0 until childCount)
          .any { getChild(it).isQuickSettingsPanelVisibleRecursive() }
      }

      return@catch !service.rootInActiveWindow.isQuickSettingsPanelVisibleRecursive()
    }.getOrElse { true }

    if (targetState)
      performAccessibilityAction(GLOBAL_ACTION_QUICK_SETTINGS)
    else
      closeSystemDialogs()
  }

  @Provide val accessibilityConfig: AccessibilityConfig
    get() = AccessibilityConfig(
      flags = AccessibilityServiceInfo.CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT or
          AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
    )
}

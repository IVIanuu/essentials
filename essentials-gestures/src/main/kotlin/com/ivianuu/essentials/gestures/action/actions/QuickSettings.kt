/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityServiceProvider
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.getOrElse
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first

@Provide object QuickSettingsActionId : ActionId("quick_settings")

context(ResourceProvider) @Provide fun quickSettingsAction() = Action(
  id = QuickSettingsActionId,
  title = loadResource(R.string.es_action_quick_settings),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(Icons.Default.Settings)
)

context(
AccessibilityServiceProvider,
AppContext,
CloseSystemDialogsUseCase,
GlobalActionExecutor,
SystemBuildInfo) @Provide @SuppressLint("NewApi")
fun quickSettingsActionExecutor() = ActionExecutor<QuickSettingsActionId> {
  val targetState = if (systemSdk < 28) true else catch {
    val service = accessibilityService.first()!!

    val systemUiContext = createPackageContext("com.android.systemui", 0)

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
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)
  else
    closeSystemDialogs()
}

@Provide val quickSettingsActionAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    flags = AccessibilityServiceInfo.CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT or
        AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
  )

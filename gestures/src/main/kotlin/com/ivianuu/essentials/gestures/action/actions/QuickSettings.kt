/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import arrow.core.Either
import arrow.core.getOrElse
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.ScopeManager
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityScope
import com.ivianuu.essentials.accessibility.AccessibilityService
import com.ivianuu.essentials.accessibility.accessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.scopeOfOrNull
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first

@Provide object QuickSettingsActionId : ActionId("quick_settings")

@Provide fun quickSettingsAction(resources: Resources) = Action(
  id = QuickSettingsActionId,
  title = resources(R.string.action_quick_settings),
  permissions = accessibilityActionPermissions,
  icon = staticActionIcon(Icons.Default.Settings)
)

@Provide
@SuppressLint("NewApi")
fun quickSettingsActionExecutor(
  accessibilityService: AccessibilityService,
  appContext: AppContext,
  closeSystemDialogs: CloseSystemDialogsUseCase,
  scopeManager: ScopeManager,
) = ActionExecutor<QuickSettingsActionId> {
  val targetState = Either.catch {
    val service = scopeManager.scopeOfOrNull<AccessibilityScope>().first()!!.accessibilityService

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
    accessibilityService.performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
  else
    closeSystemDialogs()
}

@Provide val quickSettingsActionAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    flags = AccessibilityServiceInfo.CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT or
        AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
  )

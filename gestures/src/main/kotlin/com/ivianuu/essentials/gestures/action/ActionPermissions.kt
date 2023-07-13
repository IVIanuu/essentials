/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import androidx.compose.material.Icon
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.systemoverlay.SystemOverlayPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.injekt.Provide

@Provide class ActionAccessibilityPermission(resources: Resources) :
  AccessibilityServicePermission(
    serviceClass = EsAccessibilityService::class,
    title = resources.resource(R.string.es_action_accessibility_permission_title),
    desc = resources.resource(R.string.es_action_accessibility_permission_desc),
    icon = Permission.Icon { Icon(R.drawable.es_ic_accessibility) }
  )

@Provide class ActionSystemOverlayPermission(resources: Resources) :
  SystemOverlayPermission(
    title = resources.resource(R.string.es_action_system_overlay_permission_title),
    desc = resources.resource(R.string.es_action_system_overlay_permission_desc),
    icon = Permission.Icon { Icon(R.drawable.es_ic_adb) }
  )

@Provide class ActionWriteSettingsPermission(resources: Resources) :
  WriteSettingsPermission(
    title = resources.resource(R.string.es_action_write_settings_permission_title),
    desc = resources.resource(R.string.es_action_write_settings_permission_desc),
    icon = Permission.Icon { Icon(R.drawable.es_ic_settings) }
  )

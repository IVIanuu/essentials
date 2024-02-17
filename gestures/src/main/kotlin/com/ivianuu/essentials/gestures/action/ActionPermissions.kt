/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import androidx.compose.material.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.accessibility.*
import com.ivianuu.essentials.permission.systemoverlay.*
import com.ivianuu.essentials.permission.writesettings.*
import com.ivianuu.injekt.*

@Provide class ActionAccessibilityPermission(resources: Resources) :
  AccessibilityServicePermission(
    serviceClass = EsAccessibilityService::class,
    title = resources(R.string.action_accessibility_permission_title),
    desc = resources(R.string.action_accessibility_permission_desc),
    icon = Permission.Icon { Icon(painterResource(com.ivianuu.essentials.android.R.drawable.ic_accessibility), null) }
  )

@Provide class ActionSystemOverlayPermission(resources: Resources) :
  SystemOverlayPermission(
    title = resources(R.string.action_system_overlay_permission_title),
    desc = resources(R.string.action_system_overlay_permission_desc),
    icon = Permission.Icon { Icon(painterResource(R.drawable.ic_adb), null) }
  )

@Provide class ActionWriteSettingsPermission(resources: Resources) :
  WriteSettingsPermission(
    title = resources(R.string.action_write_settings_permission_title),
    desc = resources(R.string.action_write_settings_permission_desc),
    icon = Permission.Icon { Icon(painterResource(com.ivianuu.essentials.android.R.drawable.ic_settings), null) }
  )

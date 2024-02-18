/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.permission.accessibility.*
import com.ivianuu.essentials.permission.systemoverlay.*
import com.ivianuu.essentials.permission.writesettings.*
import com.ivianuu.injekt.*

@Provide class ActionAccessibilityPermission : AccessibilityServicePermission(
  serviceClass = EsAccessibilityService::class,
  title = "Accessibility",
  desc = "Required to perform actions such as back, open notifications, or to switch the camera",
  icon = { Icon(Icons.Default.Accessibility, null) }
)

@Provide class ActionSystemOverlayPermission : SystemOverlayPermission(
    title = "System overlay",
    desc = "Required to open apps from the background",
    icon = { Icon(Icons.Default.Adb, null) }
  )

@Provide class ActionWriteSettingsPermission : WriteSettingsPermission(
    title = "Write settings",
    desc = "Required to change settings",
    icon = { Icon(Icons.Default.Settings, null) }
  )

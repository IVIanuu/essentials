/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action

import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import essentials.accessibility.*
import essentials.permission.*
import injekt.*

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

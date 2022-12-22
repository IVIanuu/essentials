/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import androidx.compose.material.Icon
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.notificationpolicy.NotificationPolicyPermission
import com.ivianuu.essentials.permission.root.RootPermission
import com.ivianuu.essentials.permission.systemoverlay.SystemOverlayPermission
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.injekt.Provide

context(ResourceProvider) @Provide class ActionAccessibilityPermission :
  AccessibilityServicePermission(
    serviceClass = EsAccessibilityService::class,
    title = loadResource(R.string.es_action_accessibility_permission_title),
    desc = loadResource(R.string.es_action_accessibility_permission_desc),
    icon = Permission.Icon { Icon(R.drawable.es_ic_accessibility) }
  )

context(ResourceProvider) @Provide class ActionNotificationPolicyPermission :
  NotificationPolicyPermission(
    title = loadResource(R.string.es_action_notification_policy_permission_title),
    desc = loadResource(R.string.es_action_notification_policy_permission_desc),
    icon = Permission.Icon { Icon(R.drawable.es_ic_notifications) }
  )

context(ResourceProvider) @Provide class ActionRootPermission : RootPermission(
  title = loadResource(R.string.es_action_root_permission_title),
  icon = Permission.Icon { Icon(R.drawable.es_ic_adb) }
)

context(ResourceProvider) @Provide class ActionSystemOverlayPermission : SystemOverlayPermission(
  title = loadResource(R.string.es_action_system_overlay_permission_title),
  desc = loadResource(R.string.es_action_system_overlay_permission_desc),
  icon = Permission.Icon { Icon(R.drawable.es_ic_adb) }
)

context(ResourceProvider) @Provide class ActionWriteSecureSettingsPermission :
  WriteSecureSettingsPermission(
    title = loadResource(R.string.es_action_write_secure_settings_permission_title),
    desc = loadResource(R.string.es_action_write_secure_settings_permission_desc),
    icon = Permission.Icon { Icon(R.drawable.es_ic_settings) }
  )

context(ResourceProvider) @Provide class ActionWriteSettingsPermission : WriteSettingsPermission(
  title = loadResource(R.string.es_action_write_settings_permission_title),
  desc = loadResource(R.string.es_action_write_settings_permission_desc),
  icon = Permission.Icon { Icon(R.drawable.es_ic_settings) }
)

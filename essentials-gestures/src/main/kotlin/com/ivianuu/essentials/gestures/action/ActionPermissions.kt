/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import androidx.compose.material.Icon
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.notificationpolicy.NotificationPolicyPermission
import com.ivianuu.essentials.permission.root.RootPermission
import com.ivianuu.essentials.permission.systemoverlay.SystemOverlayPermission
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.injekt.Provide

@Provide class ActionAccessibilityPermission(RP: ResourceProvider) : AccessibilityServicePermission(
  serviceClass = EsAccessibilityService::class,
  title = loadResource(R.string.es_action_accessibility_permission_title),
  desc = loadResource(R.string.es_action_accessibility_permission_desc),
  icon = { Icon(R.drawable.es_ic_accessibility) }
)

@Provide class ActionNotificationPolicyPermission(RP: ResourceProvider) :
  NotificationPolicyPermission(
    title = loadResource(R.string.es_action_notification_policy_permission_title),
    desc = loadResource(R.string.es_action_notification_policy_permission_desc),
    icon = { Icon(R.drawable.es_ic_notifications) }
  )

@Provide class ActionRootPermission(RP: ResourceProvider) : RootPermission(
  title = loadResource(R.string.es_action_root_permission_title),
  icon = { Icon(R.drawable.es_ic_adb) }
)

@Provide class ActionSystemOverlayPermission(RP: ResourceProvider) : SystemOverlayPermission(
  title = loadResource(R.string.es_action_system_overlay_permission_title),
  desc = loadResource(R.string.es_action_system_overlay_permission_desc),
  icon = { Icon(R.drawable.es_ic_adb) }
)

@Provide class ActionWriteSecureSettingsPermission(RP: ResourceProvider) :
  WriteSecureSettingsPermission(
    title = loadResource(R.string.es_action_write_secure_settings_permission_title),
    desc = loadResource(R.string.es_action_write_secure_settings_permission_desc),
    icon = { Icon(R.drawable.es_ic_settings) }
  )

@Provide class ActionWriteSettingsPermission(RP: ResourceProvider) : WriteSettingsPermission(
  title = loadResource(R.string.es_action_write_settings_permission_title),
  desc = loadResource(R.string.es_action_write_settings_permission_desc),
  icon = { Icon(R.drawable.es_ic_settings) }
)

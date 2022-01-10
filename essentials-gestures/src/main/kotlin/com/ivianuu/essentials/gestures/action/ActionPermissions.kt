/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import android.accessibilityservice.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.permission.accessibility.*
import com.ivianuu.essentials.permission.notificationpolicy.*
import com.ivianuu.essentials.permission.root.*
import com.ivianuu.essentials.permission.systemoverlay.*
import com.ivianuu.essentials.permission.writesecuresettings.*
import com.ivianuu.essentials.permission.writesettings.*
import com.ivianuu.injekt.*
import kotlin.reflect.*

@Provide class ActionAccessibilityPermission(private val RP: ResourceProvider) : AccessibilityServicePermission {
  override val serviceClass: KClass<out AccessibilityService>
    get() = EsAccessibilityService::class
  override val title: String
    get() = loadResource(R.string.es_action_accessibility_permission_title)
  override val desc: String
    get() = loadResource(R.string.es_action_accessibility_permission_title)

  @Composable override fun Icon() {
    Icon(R.drawable.es_ic_accessibility)
  }
}

@Provide class ActionNotificationPolicyPermission(private val RP: ResourceProvider) : NotificationPolicyPermission {
  override val title: String
    get() = loadResource(R.string.es_action_notification_policy_permission_title)
  override val desc: String
    get() = loadResource(R.string.es_action_notification_policy_permission_desc)

  @Composable override fun Icon() {
    Icon(R.drawable.es_ic_notifications)
  }
}

@Provide class ActionRootPermission(private val RP: ResourceProvider) : RootPermission {
  override val title: String
    get() = loadResource(R.string.es_action_root_permission_title)

  @Composable override fun Icon() {
    Icon(R.drawable.es_ic_adb)
  }
}

@Provide class ActionSystemOverlayPermission(private val RP: ResourceProvider) : SystemOverlayPermission {
  override val title: String
    get() = loadResource(R.string.es_action_system_overlay_permission_title)
  override val desc: String
    get() = loadResource(R.string.es_action_system_overlay_permission_desc)

  @Composable override fun Icon() {
    Icon(R.drawable.es_ic_adb)
  }
}

@Provide class ActionWriteSecureSettingsPermission(private val RP: ResourceProvider) : WriteSecureSettingsPermission {
  override val title: String
    get() = loadResource(R.string.es_action_write_secure_settings_permission_title)
  override val desc: String
    get() = loadResource(R.string.es_action_write_secure_settings_permission_desc)

  @Composable override fun Icon() {
    Icon(R.drawable.es_ic_settings)
  }
}

@Provide class ActionWriteSettingsPermission(private val RP: ResourceProvider) : WriteSettingsPermission {
  override val title: String
    get() = loadResource(R.string.es_action_write_settings_permission_title)
  override val desc: String
    get() = loadResource(R.string.es_action_write_settings_permission_desc)

  @Composable override fun Icon() {
    Icon(R.drawable.es_ic_settings)
  }
}


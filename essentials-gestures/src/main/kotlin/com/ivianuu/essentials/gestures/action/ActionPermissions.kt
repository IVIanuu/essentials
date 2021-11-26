/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.gestures.action

import android.accessibilityservice.AccessibilityService
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
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
import kotlin.reflect.KClass

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


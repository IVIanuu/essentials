/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.notificationpolicy

import android.app.NotificationManager
import android.content.Intent
import android.provider.Settings
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.essentials.permission.intent.ShowFindPermissionHint
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService

abstract class NotificationPolicyPermission(
  override val title: String,
  override val desc: String? = null,
  override val icon: Permission.Icon? = null
) : Permission

@Provide fun <P : NotificationPolicyPermission> notificationPolicyShowFindPermissionHint(
) = ShowFindPermissionHint<P>(true)

context(NotificationManager)
    @Provide fun <P : NotificationPolicyPermission> notificationPolicyPermissionStateProvider(
) = PermissionStateProvider<P> { isNotificationPolicyAccessGranted }

@Provide fun <P : NotificationPolicyPermission> notificationPolicyPermissionIntentFactory(
) = PermissionIntentFactory<P> { Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS) }

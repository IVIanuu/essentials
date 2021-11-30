/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.notificationpolicy

import android.app.*
import android.content.*
import android.provider.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

interface NotificationPolicyPermission : Permission

@Provide fun <P : NotificationPolicyPermission> notificationPolicyShowFindPermissionHint(
) = ShowFindPermissionHint<P>(true)

@Provide fun <P : NotificationPolicyPermission> notificationPolicyPermissionStateProvider(
  notificationManager: @SystemService NotificationManager
) = PermissionStateProvider<P> { notificationManager.isNotificationPolicyAccessGranted }

@Provide fun <P : NotificationPolicyPermission> notificationPolicyPermissionIntentFactory(
) = PermissionIntentFactory<P> { Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS) }

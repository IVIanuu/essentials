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
) : Permission {
  companion object {
    @Provide fun <P : NotificationPolicyPermission> showFindPermissionHint() =
      ShowFindPermissionHint<P>(true)

    @Provide fun <P : NotificationPolicyPermission> stateProvider(
      notificationManager: @SystemService NotificationManager
    ) = PermissionStateProvider<P> { notificationManager.isNotificationPolicyAccessGranted }

    @Provide fun <P : NotificationPolicyPermission> intentFactory(
    ) = PermissionIntentFactory<P> { Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS) }
  }
}


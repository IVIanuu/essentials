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

interface NotificationPolicyPermission : Permission

@Provide fun <P : NotificationPolicyPermission> notificationPolicyShowFindPermissionHint(
) = ShowFindPermissionHint<P>(true)

@Provide fun <P : NotificationPolicyPermission> notificationPolicyPermissionStateProvider(
  notificationManager: @SystemService NotificationManager
): PermissionStateProvider<P> = { notificationManager.isNotificationPolicyAccessGranted }

@Provide fun <P : NotificationPolicyPermission> notificationPolicyPermissionIntentFactory(
): PermissionIntentFactory<P> = { Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS) }

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

package com.ivianuu.essentials.permission.notificationlistener

import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.essentials.permission.intent.ShowFindPermissionHint
import com.ivianuu.injekt.Provide
import kotlin.reflect.KClass

interface NotificationListenerPermission : Permission {
  val serviceClass: KClass<out NotificationListenerService>
}

@Provide fun <P : NotificationListenerPermission> notificationListenerShowFindPermissionHint(
) = ShowFindPermissionHint<P>(true)

@Provide fun <P : NotificationListenerPermission> notificationListenerPermissionStateProvider(
  context: AppContext,
  buildInfo: BuildInfo
) = PermissionStateProvider<P> {
  NotificationManagerCompat.getEnabledListenerPackages(context)
    .any { it == buildInfo.packageName }
}

@Provide fun <P : NotificationListenerPermission> notificationListenerPermissionIntentFactory(
  buildInfo: BuildInfo
) = PermissionIntentFactory<P> { permission ->
  Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
    val componentName = "${buildInfo.packageName}/${permission.serviceClass.java.name}"
    putExtra(":settings:fragment_args_key", componentName)
    putExtra(
      ":settings:show_fragment_args", bundleOf(
        ":settings:fragment_args_key" to componentName
      )
    )
  }
}

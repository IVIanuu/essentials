/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.notificationlistener

import android.content.*
import android.provider.*
import android.service.notification.*
import androidx.core.app.*
import androidx.core.os.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import kotlin.reflect.*

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

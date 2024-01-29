/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.notificationlistener

import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.essentials.permission.intent.ShowFindPermissionHint
import com.ivianuu.injekt.Provide
import kotlin.reflect.KClass

abstract class NotificationListenerPermission(
  val serviceClass: KClass<out NotificationListenerService>,
  override val title: String,
  override val desc: String? = null,
  override val icon: Permission.Icon? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : NotificationListenerPermission> showFindPermissionHint() =
      ShowFindPermissionHint<P>(true)

    @Provide fun <P : NotificationListenerPermission> stateProvider(
      appContext: AppContext,
      appConfig: AppConfig
    ) = PermissionStateProvider<P> {
      NotificationManagerCompat.getEnabledListenerPackages(appContext)
        .any { it == appConfig.packageName }
    }

    @Provide fun <P : NotificationListenerPermission> intentFactory(
      appConfig: AppConfig
    ) = PermissionIntentFactory<P> { permission ->
      Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
        val componentName = "${appConfig.packageName}/${permission.serviceClass.java.name}"
        putExtra(":settings:fragment_args_key", componentName)
        putExtra(
          ":settings:show_fragment_args", bundleOf(
            ":settings:fragment_args_key" to componentName
          )
        )
      }
    }
  }
}

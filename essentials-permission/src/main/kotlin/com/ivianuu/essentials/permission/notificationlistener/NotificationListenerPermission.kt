/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.notificationlistener

import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import androidx.compose.runtime.Composable
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

abstract class NotificationListenerPermission(
  val serviceClass: KClass<out NotificationListenerService>,
  override val title: String,
  override val desc: String? = null,
  override val icon: @Composable () -> Unit = { Permission.NullIcon }
) : Permission {
  companion object {
    @Provide fun <P : NotificationListenerPermission> showFindPermissionHint() =
      ShowFindPermissionHint<P>(true)

    @Provide fun <P : NotificationListenerPermission> stateProvider(
      appContext: AppContext,
      buildInfo: BuildInfo
    ) = PermissionStateProvider<P> {
      NotificationManagerCompat.getEnabledListenerPackages(appContext)
        .any { it == buildInfo.packageName }
    }

    @Provide fun <P : NotificationListenerPermission> intentFactory(
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
  }
}

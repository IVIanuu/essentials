/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.notificationlistener

import android.content.*
import android.provider.*
import android.service.notification.*
import androidx.compose.runtime.*
import androidx.core.app.*
import androidx.core.os.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.injekt.*
import kotlin.reflect.*

abstract class NotificationListenerPermission(
  val serviceClass: KClass<out NotificationListenerService>,
  override val title: String,
  override val desc: String? = null,
  override val icon: (@Composable () -> Unit)? = null
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

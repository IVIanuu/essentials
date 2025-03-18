/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import android.app.*
import android.content.*
import android.provider.*
import android.service.notification.*
import androidx.compose.runtime.*
import androidx.core.app.*
import androidx.core.os.*
import essentials.*
import injekt.*
import kotlin.reflect.*

abstract class NotificationListenerPermission(
  val serviceClass: KClass<out NotificationListenerService>,
  override val title: String,
  override val desc: String,
  override val icon: (@Composable () -> Unit)? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : NotificationListenerPermission> state(
      context: Application,
      appConfig: AppConfig
    ): PermissionState<P> =
      NotificationManagerCompat.getEnabledListenerPackages(context)
        .any { it == appConfig.packageName }

    @Provide fun <P : NotificationListenerPermission> requestParams(
      permission: P,
      appConfig: AppConfig
    ) = IntentPermissionRequestParams<P>(
      intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
        val componentName = "${appConfig.packageName}/${permission.serviceClass.java.name}"
        putExtra(":settings:fragment_args_key", componentName)
        putExtra(
          ":settings:show_fragment_args", bundleOf(
            ":settings:fragment_args_key" to componentName
          )
        )
      },
      showFindHint = true
    )
  }
}

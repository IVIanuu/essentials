/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import android.accessibilityservice.*
import android.app.*
import android.content.*
import android.provider.*
import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import androidx.core.os.*
import essentials.*
import injekt.*
import kotlin.reflect.*

abstract class AccessibilityServicePermission(
  val serviceClass: KClass<out AccessibilityService>,
  override val title: String,
  override val desc: String,
  override val icon: (@Composable () -> Unit)? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : AccessibilityServicePermission> state(
      context: Application
    ): PermissionState<P> = Settings.Secure.getString(
      context.contentResolver,
      Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
      ?.split(":")
      ?.fastMap { it.split("/").first() }
      ?.fastAny { it == context.packageName } == true

    @Provide fun <P : AccessibilityServicePermission> requestParams(
      permission: P,
      appConfig: AppConfig
    ) = IntentPermissionRequestParams<P>(
      intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
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

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import android.accessibilityservice.*
import android.content.*
import android.provider.*
import androidx.compose.runtime.*
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastMap
import androidx.core.os.*
import essentials.*
import injekt.*
import kotlin.reflect.*

abstract class AccessibilityServicePermission(
  val serviceClass: KClass<out AccessibilityService>,
  override val title: String,
  override val desc: String? = null,
  override val icon: (@Composable () -> Unit)? = null
) : Permission {
  @Provide companion object {
    @Provide fun <P : AccessibilityServicePermission> stateProvider(
      appContext: AppContext
    ) = PermissionStateProvider<P> provider@{
      Settings.Secure.getString(
        appContext.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
      )
        ?.split(":")
        ?.fastMap {
          it.split("/").first()
        }
        ?.fastAny { it == appContext.packageName } == true
    }

    @Provide fun <P : AccessibilityServicePermission> showFindPermissionHint() =
      ShowFindPermissionHint<P>(true)

    @Provide fun <P : AccessibilityServicePermission> intentFactory(
      appConfig: AppConfig
    ) = PermissionIntentFactory<P> { permission ->
      Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
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

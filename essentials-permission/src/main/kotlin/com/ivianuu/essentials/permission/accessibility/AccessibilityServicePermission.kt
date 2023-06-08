/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.essentials.permission.intent.ShowFindPermissionHint
import com.ivianuu.injekt.Provide
import kotlin.reflect.KClass

abstract class AccessibilityServicePermission(
  val serviceClass: KClass<out AccessibilityService>,
  override val title: String,
  override val desc: String? = null,
  override val icon: @Composable () -> Unit = { Permission.NullIcon }
) : Permission {
  companion object {
    @Provide fun <P : AccessibilityServicePermission> stateProvider(
      appContext: AppContext
    ) = PermissionStateProvider<P> provider@{
      Settings.Secure.getString(
        appContext.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
      )
        ?.split(":")
        ?.map {
          it.split("/").first()
        }
        ?.any { it == appContext.packageName } == true
    }

    @Provide fun <P : AccessibilityServicePermission> showFindPermissionHint() =
      ShowFindPermissionHint<P>(true)

    @Provide fun <P : AccessibilityServicePermission> intentFactory(
      buildInfo: BuildInfo
    ) = PermissionIntentFactory<P> { permission ->
      Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
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

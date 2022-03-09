/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.accessibility

import android.accessibilityservice.*
import android.content.*
import android.provider.*
import android.text.*
import androidx.core.os.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import kotlin.reflect.*

interface AccessibilityServicePermission : Permission {
  val serviceClass: KClass<out AccessibilityService>
}

@Provide fun <P : AccessibilityServicePermission> accessibilityServicePermissionStateProvider(
  context: AppContext
) = PermissionStateProvider<P> provider@ { permission ->
  val expectedComponentName = ComponentName(context, permission.serviceClass.java)

  val enabledServicesSetting = Settings.Secure.getString(
    context.contentResolver,
    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
  ) ?: return@provider false

  val colonSplitter = TextUtils.SimpleStringSplitter(':')
  colonSplitter.setString(enabledServicesSetting)

  while (colonSplitter.hasNext()) {
    val componentNameString: String = colonSplitter.next()
    val enabledService = ComponentName.unflattenFromString(componentNameString)
    if (enabledService != null && enabledService == expectedComponentName) return@provider true
  }

  return@provider false
}

@Provide fun <P : AccessibilityServicePermission> accessibilityServiceShowFindPermissionHint(
): ShowFindPermissionHint<P> = ShowFindPermissionHint(true)

@Provide fun <P : AccessibilityServicePermission> accessibilityServicePermissionIntentFactory(
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

/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.permission.accessibility

import android.accessibilityservice.*
import android.content.*
import android.content.ComponentName
import android.provider.*
import android.text.*
import android.view.accessibility.*
import androidx.core.os.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.intent.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import kotlin.reflect.*

interface AccessibilityServicePermission : Permission {
  val serviceClass: KClass<out AccessibilityService>
}

@Provide fun <P : AccessibilityServicePermission> accessibilityServicePermissionStateProvider(
  context: AppContext
): PermissionStateProvider<P> = provider@{ permission ->
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
): ShowFindPermissionHint<P> = true

@Provide fun <P : AccessibilityServicePermission> accessibilityServicePermissionIntentFactory(
  buildInfo: BuildInfo
): PermissionIntentFactory<P> = { permission ->
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

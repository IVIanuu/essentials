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

package com.ivianuu.essentials.gestures.action

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.accessibility.DefaultAccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.permission.PermissionBinding
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.root.RootPermission
import com.ivianuu.essentials.permission.ui.PermissionUiMetadata
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.essentials.ui.core.Icon
import com.ivianuu.injekt.Given
import kotlin.reflect.KClass

@PermissionBinding
@Given
object ActionAccessibilityPermission : AccessibilityServicePermission {
    override val serviceClass: KClass<out AccessibilityService>
        get() = DefaultAccessibilityService::class
}

@Given
val actionAccessibilityPermissionMetadata = PermissionUiMetadata<ActionAccessibilityPermission>(
    title = "Accessibility", // todo res
    desc = "Required to click buttons", // todo res
    icon = { Icon(R.drawable.es_ic_accessibility, null) }
)

@PermissionBinding
@Given
object ActionRootPermission : RootPermission

@Given
val actionRootPermissionMetadata = PermissionUiMetadata<ActionRootPermission>(
    title = "Root", // todo res
    icon = { Icon(R.drawable.es_ic_adb, null) }
)

@PermissionBinding
@Given
object ActionWriteSecureSettingsPermission : WriteSecureSettingsPermission

@Given
val actionWriteSecureSettingsPermissionMetadata = PermissionUiMetadata<ActionWriteSecureSettingsPermission>(
    title = "Write secure settings", // todo res
    desc = "Required to change the navigation bar visibility", // todo res
    icon = { Icon(R.drawable.es_ic_settings, null) }
)

@PermissionBinding
@Given
object ActionWriteSettingsPermission : WriteSettingsPermission

@Given
val actionWriteSettingsPermissionMetadata = PermissionUiMetadata<ActionWriteSettingsPermission>(
    title = "Write Settings", // todo res
    desc = "Required to change settings", // todo res
    icon = { Icon(R.drawable.es_ic_settings, null) } // todo change icon
)

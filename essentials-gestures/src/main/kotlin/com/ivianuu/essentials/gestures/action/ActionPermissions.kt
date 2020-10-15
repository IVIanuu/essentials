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

import androidx.compose.foundation.Icon
import androidx.compose.ui.res.vectorResource
import com.ivianuu.essentials.accessibility.DefaultAccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.root.RootPermission
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.android.ApplicationContext
import com.ivianuu.injekt.merge.ApplicationComponent

@Binding(ApplicationComponent::class)
class ActionPermissions(applicationContext: ApplicationContext) {
    val accessibility = AccessibilityServicePermission(
        DefaultAccessibilityService::class,
        Permission.Title withValue "Accessibility", // todo res
        Permission.Desc withValue "Required to click buttons", // todo res
        Permission.Icon withValue { Icon(vectorResource(R.drawable.es_ic_accessibility)) }
    )
    val root = RootPermission(
        Permission.Title withValue "Root", // todo res
        Permission.Icon withValue { Icon(vectorResource(R.drawable.es_ic_adb)) }
    )
    val writeSecureSettings = WriteSecureSettingsPermission(
        Permission.Title withValue "Write secure settings", // todo res
        Permission.Desc withValue "Required to change the navigation bar visibility", // todo res
        Permission.Icon withValue { Icon(vectorResource(R.drawable.es_ic_settings)) }
    )
    val writeSettings = WriteSettingsPermission(
        applicationContext,
        Permission.Title withValue "Write Settings", // todo res
        Permission.Desc withValue "Required to change settings", // todo res
        Permission.Icon withValue { Icon(vectorResource(R.drawable.es_ic_settings)) } // todo change icon
    )
}

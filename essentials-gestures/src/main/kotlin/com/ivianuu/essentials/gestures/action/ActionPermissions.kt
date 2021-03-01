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
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.permission.PermissionBinding
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.essentials.permission.root.RootPermission
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermission
import com.ivianuu.injekt.Given
import kotlin.reflect.KClass

@PermissionBinding
@Given
object ActionAccessibilityPermission : AccessibilityServicePermission {
    override val serviceClass: KClass<out AccessibilityService>
        get() = EsAccessibilityService::class
    override val title: String = "Accessibility"
    override val desc: String = "Required to click buttons"
    override val icon: @Composable () -> Unit = {
        Icon(painterResource(R.drawable.es_ic_accessibility), null)
    }
}

@PermissionBinding
@Given
object ActionRootPermission : RootPermission {
    override val title = "Root" // todo res
    override val icon: @Composable () -> Unit = {
        Icon(painterResource(R.drawable.es_ic_adb), null)
    }
}

@PermissionBinding
@Given
object ActionWriteSecureSettingsPermission : WriteSecureSettingsPermission {
    override val title: String = "Write secure settings" // todo res
    override val desc: String = "Required to change the navigation bar visibility" // todo res
    override val icon: @Composable () -> Unit = {
        Icon(painterResource(R.drawable.es_ic_settings), null)
    }
}

@PermissionBinding
@Given
object ActionWriteSettingsPermission : WriteSettingsPermission {
    override val title: String = "Write Settings" // todo res
    override val desc: String = "Required to change settings" // todo res
    override val icon: @Composable () -> Unit = {
        Icon(painterResource(R.drawable.es_ic_settings), null)
    } // todo change icon
}

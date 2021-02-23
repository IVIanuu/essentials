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

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.Given
import kotlin.reflect.KClass

interface AccessibilityServicePermission : Permission {
    val serviceClass: KClass<out AccessibilityService>
}

@Given
fun <P : AccessibilityServicePermission> accessibilityServicePermissionStateProvider(
    @Given accessibilityManager: AccessibilityManager,
    @Given buildInfo: BuildInfo
): PermissionStateProvider<P> = { permission ->
    accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        .any {
            it.resolveInfo.serviceInfo.packageName == buildInfo.packageName &&
                    it.resolveInfo.serviceInfo.name == permission.serviceClass.java.canonicalName
        }
}

@Given
fun <P : AccessibilityServicePermission> accessibilityServicePermissionIntentFactory():
        PermissionIntentFactory<P> = { Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS) }

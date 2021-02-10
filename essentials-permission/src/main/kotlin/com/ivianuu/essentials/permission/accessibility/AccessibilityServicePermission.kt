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
import com.ivianuu.essentials.permission.PermissionStateProviderBinding
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.to
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.Given
import kotlin.reflect.KClass

fun AccessibilityServicePermission(
    serviceClass: KClass<out AccessibilityService>,
    vararg metadata: Permission.Pair<*>
) = Permission(
    Permission.AccessibilityServiceClass to serviceClass,
    Permission.Intent to Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
    *metadata
)

val Permission.Companion.AccessibilityServiceClass by lazy {
    Permission.Key<KClass<out AccessibilityService>>(
        "AccessibilityServiceClass"
    )
}

@PermissionStateProviderBinding
@Given
class AccessibilityServicePermissionStateProvider(
    @Given private val accessibilityManager: AccessibilityManager,
    @Given private val buildInfo: BuildInfo,
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.AccessibilityServiceClass in permission

    override suspend fun isGranted(permission: Permission): Boolean {
        return accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any {
                val r = it.resolveInfo.serviceInfo.packageName == buildInfo.packageName &&
                    it.resolveInfo.serviceInfo.name == permission[Permission.AccessibilityServiceClass].java.canonicalName
                r
            }
    }
}

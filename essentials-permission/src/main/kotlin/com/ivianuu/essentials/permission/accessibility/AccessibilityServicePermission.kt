/*
 * Copyright 2019 Manuel Wrage
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
import com.ivianuu.essentials.permission.MetaDataKeyWithValue
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.bindPermissionStateProviderIntoSet
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import kotlin.reflect.KClass

fun AccessibilityServicePermission(
    serviceClass: KClass<out AccessibilityService>,
    vararg metadata: MetaDataKeyWithValue<*>
) = Permission(
    metadata = metadataOf(
        Metadata.AccessibilityServiceClass withValue serviceClass,
        Metadata.Intent withValue Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
        *metadata
    )
)

val Metadata.Companion.AccessibilityServiceClass by lazy {
    Metadata.Key<KClass<out AccessibilityService>>(
        "AccessibilityServiceClass"
    )
}

internal fun ComponentBuilder.accessibilityPermission() {
    bindPermissionStateProviderIntoSet<AccessibilityServicePermissionStateProvider>()
}

@Factory
private class AccessibilityServicePermissionStateProvider(
    private val accessibilityManager: AccessibilityManager,
    private val buildInfo: BuildInfo
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.AccessibilityServiceClass in permission.metadata

    override suspend fun isGranted(permission: Permission): Boolean {
        return accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any {
                it.resolveInfo.serviceInfo.packageName == buildInfo.packageName &&
                        it.resolveInfo.serviceInfo.name == permission.metadata[Metadata.AccessibilityServiceClass].java.canonicalName
            }
    }
}

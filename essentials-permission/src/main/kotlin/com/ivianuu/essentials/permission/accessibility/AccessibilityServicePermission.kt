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
import android.app.ActivityManager
import android.content.Intent
import android.provider.Settings
import com.ivianuu.essentials.permission.MetaDataKeyWithValue
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.permission.with
import com.ivianuu.injekt.Factory
import kotlin.reflect.KClass

fun AccessibilityServicePermission(
    serviceClass: KClass<out AccessibilityService>,
    vararg metadata: MetaDataKeyWithValue<*>
) = Permission(
    metadata = metadataOf(
        Metadata.AccessibilityServiceClass with serviceClass,
        Metadata.Intent with Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
        *metadata
    )
)

val Metadata.Companion.AccessibilityServiceClass by lazy {
    Metadata.Key<KClass<out AccessibilityService>>(
        "AccessibilityServiceClass"
    )
}

@Factory
class AccessibilityServicePermissionStateProvider(
    private val activityManager: ActivityManager
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.AccessibilityServiceClass in permission.metadata

    override suspend fun isGranted(permission: Permission): Boolean =
        activityManager.getRunningServices(Int.MAX_VALUE)
            .any { it.service.className == permission.metadata[Metadata.AccessibilityServiceClass].java.canonicalName }
}

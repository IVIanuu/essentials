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

package com.ivianuu.essentials.permission.notificationlistener

import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationManagerCompat
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.PermissionStateProviderBinding
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.to
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.android.ApplicationContext
import kotlin.reflect.KClass

fun NotificationListenerPermission(
    serviceClass: KClass<out NotificationListenerService>,
    vararg metadata: Permission.Pair<*>
) = Permission(
    Permission.NotificationListenerClass to serviceClass,
    Permission.Intent to Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
    *metadata
)

val Permission.Companion.NotificationListenerClass by lazy {
    Permission.Key<KClass<out NotificationListenerService>>(
        "NotificationListenerClass"
    )
}

@PermissionStateProviderBinding
class NotificationListenerPermissionStateProvider(
    private val applicationContext: ApplicationContext,
    private val buildInfo: BuildInfo,
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.NotificationListenerClass in permission

    override suspend fun isGranted(permission: Permission): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(applicationContext)
            .any { it == buildInfo.packageName }
    }
}

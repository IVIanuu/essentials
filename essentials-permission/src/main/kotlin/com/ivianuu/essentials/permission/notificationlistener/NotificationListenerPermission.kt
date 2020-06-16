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

package com.ivianuu.essentials.permission.notificationlistener

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import com.ivianuu.essentials.permission.BindPermissionStateProvider
import com.ivianuu.essentials.permission.KeyWithValue
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.Intent
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Transient
import kotlin.reflect.KClass

fun NotificationListenerPermission(
    serviceClass: KClass<out NotificationListenerService>,
    vararg metadata: KeyWithValue<*>
) = Permission(
    Permission.NotificationListenerClass withValue serviceClass,
    Permission.Intent withValue Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
    *metadata
)

val Permission.Companion.NotificationListenerClass by lazy {
    Permission.Key<KClass<out NotificationListenerService>>(
        "NotificationListenerClass"
    )
}

@BindPermissionStateProvider
@Transient
internal class NotificationListenerPermissionStateProvider(
    private val buildInfo: BuildInfo,
    private val context: @ForApplication Context
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Permission.NotificationListenerClass in permission

    override suspend fun isGranted(permission: Permission): Boolean {
        return Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
            .split(":")
            .map {
                val tmp = it.split("/")
                tmp[0] to tmp[1]
            }
            .any { (packageName, listenerName) ->
                packageName == buildInfo.packageName &&
                        listenerName == permission[Permission.NotificationListenerClass].java.canonicalName
            }
    }
}

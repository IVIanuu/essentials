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

package com.ivianuu.essentials.permission.runtime

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.onActive
import androidx.compose.remember
import com.ivianuu.essentials.permission.BindPermissionRequestHandler
import com.ivianuu.essentials.permission.BindPermissionStateProvider
import com.ivianuu.essentials.permission.MetaDataKeyWithValue
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.ui.common.registerActivityResultCallback
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.RouteAmbient
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Transient

fun RuntimePermission(
    name: String,
    vararg metadata: MetaDataKeyWithValue<*>
) = Permission(
    metadata = metadataOf(
        Metadata.RuntimePermissionName withValue name,
        *metadata
    )
)

val Metadata.Companion.RuntimePermissionName by lazy {
    Metadata.Key<String>("RuntimePermissionName")
}

@BindPermissionStateProvider
@Transient
internal class RuntimePermissionStateProvider(
    private val context: @ForApplication Context,
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.RuntimePermissionName in permission.metadata

    override suspend fun isGranted(permission: Permission): Boolean =
        context.checkSelfPermission(permission.metadata[Metadata.RuntimePermissionName]) ==
                PackageManager.PERMISSION_GRANTED
}

@BindPermissionRequestHandler
@Transient
internal class RuntimePermissionRequestHandler(
    private val navigator: Navigator
) : PermissionRequestHandler {

    override fun handles(permission: Permission): Boolean =
        Metadata.RuntimePermissionName in permission.metadata

    override suspend fun request(permission: Permission) {
        navigator.push<Unit>(
            Route(opaque = true) {
                val route = RouteAmbient.current
                val launcher = registerActivityResultCallback(
                    remember { ActivityResultContracts.RequestPermission() }
                ) {
                    navigator.pop(route = route)
                }

                onActive {
                    launcher.launch(permission.metadata[Metadata.RuntimePermissionName])
                }
            }
        ).await()
    }
}

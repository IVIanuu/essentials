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

package com.ivianuu.essentials.permission

import android.content.Context
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Single
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

@ApplicationScope
@Single
class PermissionManager(
    private val context: Context,
    @ForApplication private val coroutineScope: CoroutineScope,
    private val logger: Logger,
    @PermissionStateProvidersSet private val permissionStateProviders: Set<PermissionStateProvider>
) {

    private val requests = mutableMapOf<String, PermissionRequest>()

    suspend fun hasPermissions(vararg permissions: Permission): Boolean =
        hasPermissions(permissions.toList())

    suspend fun hasPermissions(permissions: List<Permission>): Boolean =
        permissions.all { stateProviderFor(it).isGranted(it) }

    suspend fun request(vararg permissions: Permission): Boolean =
        request(permissions.toList())

    suspend fun request(permissions: List<Permission>): Boolean {
        logger.d("request permissions $permissions")
        if (hasPermissions(permissions)) return true

        val id = UUID.randomUUID().toString()
        val finished = CompletableDeferred<Unit>()
        val request = PermissionRequest(
            id = id,
            permissions = permissions.toList(),
            onComplete = {
                coroutineScope.launch {
                    finished.complete(Unit)
                    requests.remove(id)
                }
            }
        )
        requests[id] = request

        PermissionActivity.request(context, id)

        finished.await()

        return hasPermissions(permissions)
    }

    internal fun getRequest(id: String): PermissionRequest? = requests[id]

    private fun stateProviderFor(permission: Permission): PermissionStateProvider =
        permissionStateProviders.firstOrNull { it.handles(permission) }
            ?: error("Couln't find state provider for $permission")
}

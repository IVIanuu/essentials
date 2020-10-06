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

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.startUi
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

@Binding(ApplicationComponent::class)
class PermissionManager(
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
    private val navigator: Navigator,
    lazyPermissionRequestRouteFactory: () -> PermissionRequestRouteFactory,
    lazyPermissionRequestHandlers: () -> Set<PermissionRequestHandler>,
    lazyPermissionStateProviders: () -> Set<PermissionStateProvider>,
    private val startUi: startUi,
) {

    private val permissionRequestRouteFactory by lazy(lazyPermissionRequestRouteFactory)
    private val permissionRequestHandlers by lazy(lazyPermissionRequestHandlers)
    private val permissionStateProviders by lazy(lazyPermissionStateProviders)
    private val permissionChanges = EventFlow<Unit>()

    fun hasPermissions(vararg permissions: Permission): Flow<Boolean> =
        hasPermissions(permissions.toList())

    fun hasPermissions(permissions: List<Permission>): Flow<Boolean> {
        return permissionChanges
            .map { Unit }
            .onStart { emit(Unit) }
            .map {
                withContext(dispatchers.default) {
                    permissions.all { stateProviderForPermission(it).isGranted(it) }
                }
            }
            .distinctUntilChanged()
    }

    suspend fun requestPermissions(vararg permissions: Permission): Boolean =
        requestPermissions(permissions.toList())

    suspend fun requestPermissions(permissions: List<Permission>): Boolean =
        withContext(dispatchers.default) {
            logger.d("request permissions $permissions")
            if (hasPermissions(permissions).first()) return@withContext true

            val request = PermissionRequest(permissions = permissions.toList())
            startUi()
            navigator.push<Any>(permissionRequestRouteFactory.createRoute(request))

            return@withContext hasPermissions(permissions).first()
        }

    fun stateProviderForPermission(permission: Permission): PermissionStateProvider =
        permissionStateProviders.firstOrNull { it.handles(permission) }
            ?: error("Couldn't find state provider for $permission")

    fun requestHandlerForPermission(permission: Permission): PermissionRequestHandler {
        val original = permissionRequestHandlers.firstOrNull { it.handles(permission) }
            ?: error("Couldn't find request handler for $permission")
        return object : PermissionRequestHandler {
            override fun handles(permission: Permission): Boolean = original.handles(permission)
            override suspend fun request(permission: Permission) {
                original.request(permission)
                permissionChanges.offer(Unit)
            }
        }
    }

}

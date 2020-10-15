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

package com.ivianuu.essentials.permission

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.DefaultDispatcher
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.startUi
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

@FunBinding
fun hasPermissions(
    defaultDispatcher: DefaultDispatcher,
    stateProvider: stateProvider,
    permissions: @Assisted List<Permission>,
): Flow<Boolean> {
    return permissionChanges
        .map { Unit }
        .onStart { emit(Unit) }
        .map {
            withContext(defaultDispatcher) {
                permissions.all { it.stateProvider().isGranted(it) }
            }
        }.distinctUntilChanged()
}

@FunBinding
suspend fun requestPermissions(
    defaultDispatcher: DefaultDispatcher,
    hasPermissions: hasPermissions,
    logger: Logger,
    navigator: Navigator,
    permissionRequestRouteFactory: PermissionRequestRouteFactory,
    startUi: startUi,
    permissions: @Assisted List<Permission>,
): Boolean = withContext(defaultDispatcher) {
    logger.d("request permissions $permissions")
    if (hasPermissions(permissions).first()) return@withContext true

    val request = PermissionRequest(permissions = permissions.toList())
    startUi()
    navigator.push<Any>(permissionRequestRouteFactory.createRoute(request))

    return@withContext hasPermissions(permissions).first()
}

@FunBinding
fun @Assisted Permission.stateProvider(
    stateProviders: Set<PermissionStateProvider>,
): PermissionStateProvider {
    return stateProviders.firstOrNull { it.handles(this) }
        ?: error("Couldn't find state provider for $this")
}

@FunBinding
fun @Assisted Permission.requestHandler(
    requestHandlers: Set<PermissionRequestHandler>,
): PermissionRequestHandler {
    val original = requestHandlers.firstOrNull { it.handles(this) }
        ?: error("Couldn't find request handler for $this")
    return object : PermissionRequestHandler {
        override fun handles(permission: Permission): Boolean = original.handles(permission)
        override suspend fun request(permission: Permission) {
            original.request(permission)
            permissionChanges.offer(Unit)
        }
    }
}

internal val permissionChanges = EventFlow<Unit>()

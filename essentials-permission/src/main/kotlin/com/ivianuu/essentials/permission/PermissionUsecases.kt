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
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.essentials.util.startUi
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

@Reader
fun hasPermissions(vararg permissions: Permission): Flow<Boolean> =
    hasPermissions(permissions.toList())

@Reader
fun hasPermissions(permissions: List<Permission>): Flow<Boolean> {
    return permissionChanges
        .map { Unit }
        .onStart { emit(Unit) }
        .map {
            withContext(dispatchers.default) {
                permissions.all { it.stateProvider.isGranted(it) }
            }
        }
        .distinctUntilChanged()
}

@Reader
suspend fun requestPermissions(vararg permissions: Permission): Boolean =
    requestPermissions(permissions.toList())

@Reader
suspend fun requestPermissions(permissions: List<Permission>): Boolean =
    withContext(dispatchers.default) {
        d { "request permissions $permissions" }
        if (hasPermissions(permissions).first()) return@withContext true

        val request = PermissionRequest(permissions = permissions.toList())
        startUi()
        navigator.push<Any>(given<PermissionRequestRouteFactory>().createRoute(request))

        return@withContext hasPermissions(permissions).first()
    }

@Reader
val Permission.stateProvider: PermissionStateProvider
    get() = given<Set<PermissionStateProvider>>().firstOrNull { it.handles(this) }
        ?: error("Couldn't find state provider for $this")

@Reader
val Permission.requestHandler: PermissionRequestHandler
    get() {
        val original = given<Set<PermissionRequestHandler>>().firstOrNull { it.handles(this) }
            ?: error("Couldn't find request handler for $this")
        return object : PermissionRequestHandler {
            override fun handles(permission: Permission): Boolean = original.handles(permission)
            override suspend fun request(permission: Permission) {
                original.request(permission)
                permissionChanges.offer(Unit)
            }
        }
    }

private val permissionChanges = EventFlow<Unit>()

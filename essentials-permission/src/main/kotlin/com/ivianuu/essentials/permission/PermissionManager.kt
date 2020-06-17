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
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.StartUi
import com.ivianuu.injekt.ApplicationScoped
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Lazy
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.UUID

@ApplicationScoped
class PermissionManager(
    private val scope: @ForApplication CoroutineScope,
    private val logger: Logger,
    private val navigator: Navigator,
    private val permissionRequestRouteFactory: @Lazy () -> PermissionRequestRouteFactory,
    private val permissionStateProviders: Set<PermissionStateProvider>,
    private val startUi: StartUi
) {

    private val requests = mutableMapOf<String, PermissionRequest>()

    private val permissionChanges = EventFlow<Unit>()

    fun hasPermissions(vararg permissions: Permission): Flow<Boolean> =
        hasPermissions(permissions.toList())

    fun hasPermissions(permissions: List<Permission>): Flow<Boolean> {
        return permissionChanges
            .map { Unit }
            .onStart { emit(Unit) }
            .map { permissions.all { stateProviderFor(it).isGranted(it) } }
            .distinctUntilChanged()
    }

    suspend fun request(vararg permissions: Permission): Boolean =
        request(permissions.toList())

    suspend fun request(permissions: List<Permission>): Boolean {
        logger.d("request permissions $permissions")
        if (hasPermissions(permissions).first()) return true

        val id = UUID.randomUUID().toString()
        val finished = CompletableDeferred<Unit>()
        val request = PermissionRequest(
            id = id,
            permissions = permissions.toList(),
            onComplete = {
                scope.launch {
                    finished.complete(Unit)
                    requests.remove(id)
                }
            }
        )
        requests[id] = request

        startUi()
        navigator.push(permissionRequestRouteFactory().createRoute(request))

        finished.await()

        return hasPermissions(permissions).first()
    }

    internal fun permissionRequestFinished() {
        permissionChanges.offer(Unit)
    }

    private fun stateProviderFor(permission: Permission): PermissionStateProvider =
        permissionStateProviders.firstOrNull { it.handles(permission) }
            ?: error("Couln't find state provider for $permission")
}

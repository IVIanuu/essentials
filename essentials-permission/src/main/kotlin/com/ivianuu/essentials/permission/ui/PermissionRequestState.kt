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

package com.ivianuu.essentials.permission.ui

import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.ui.PermissionRequestAction.RequestPermission
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@UiStateBinding
@Given
fun permissionRequestUiState(
    @Given scope: CoroutineScope,
    @Given initial: @Initial PermissionRequestState = PermissionRequestState(),
    @Given actions: Actions<PermissionRequestAction>,
    @Given appUiStarter: AppUiStarter,
    @Given key: PermissionRequestKey,
    @Given navigator: DispatchAction<NavigationAction>,
    @Given permissions: Map<TypeKey<Permission>, Permission>,
    @Given permissionStateFactory: PermissionStateFactory,
    @Given requestHandlers: Map<TypeKey<Permission>, PermissionRequestHandler<Permission>>
): StateFlow<PermissionRequestState> = scope.state(initial) {
    state
        .filter {
            key.permissionsKeys
                .all { permissionStateFactory(listOf(it)).first() }
        }
        .take(1)
        .onEach { navigator(Pop(key, true)) }
        .launchIn(this)

    suspend fun updatePermissions() {
        val permissions = key.permissionsKeys
            .filterNot { permissionStateFactory(listOf(it)).first() }
            .map { UiPermission(it, permissions[it]!!) }
        reduce { copy(permissions = permissions) }
    }

    launch { updatePermissions() }

    actions
        .filterIsInstance<RequestPermission>()
        .onEach { action ->
            requestHandlers[action.permission.permissionKey]!!(permissions[action.permission.permissionKey]!!)
            appUiStarter()
            updatePermissions()
        }
        .launchIn(this)
}

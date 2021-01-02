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

package com.ivianuu.essentials.permission.defaultui

import com.ivianuu.essentials.permission.PermissionRequest
import com.ivianuu.essentials.permission.defaultui.PermissionAction.RequestPermission
import com.ivianuu.essentials.permission.hasPermissions
import com.ivianuu.essentials.permission.requestHandler
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.store.UiStateBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import com.ivianuu.essentials.util.openAppUi
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.first

@UiStateBinding @Given
fun defaultPermissionState(
    @Given scope: CoroutineScope,
    @Given initial: @Initial PermissionState = PermissionState(),
    @Given actions: Actions<PermissionAction>,
    @Given dispatchNavigationAction: DispatchAction<NavigationAction>,
    @Given hasPermissions: hasPermissions,
    @Given key: DefaultPermissionKey,
    @Given openAppUi: openAppUi,
    @Given requestHandler: requestHandler,
) = scope.state(initial) {
    state
        .filter {
            key.request.permissions
                .all { hasPermissions(listOf(it)).first() }
        }
        .take(1)
        .onEach { dispatchNavigationAction(NavigationAction.PopTop()) }
        .launchIn(this)

    suspend fun updatePermissions() {
        val permissions = key.request.permissions
            .filterNot { hasPermissions(listOf(it)).first() }
        reduce { copy(permissions = permissions) }
    }

    launch { updatePermissions() }

    actions
        .filterIsInstance<RequestPermission>()
        .onEach { action ->
            action.permission.requestHandler().request(action.permission)
            openAppUi()
            updatePermissions()
        }
        .launchIn(this)
}

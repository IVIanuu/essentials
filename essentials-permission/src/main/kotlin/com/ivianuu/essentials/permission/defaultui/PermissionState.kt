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
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.popTop
import com.ivianuu.essentials.ui.store.Initial
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.essentials.util.startUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@UiStateBinding
fun permissionState(
    scope: CoroutineScope,
    initial: @Initial PermissionState = PermissionState(),
    actions: Actions<PermissionAction>,
    hasPermissions: hasPermissions,
    navigator: Navigator,
    request: PermissionRequest,
    requestHandler: requestHandler,
    startUi: startUi
) = scope.state(initial) {
    state
        .filter {
            request.permissions
                .all { hasPermissions(listOf(it)).first() }
        }
        .take(1)
        .onEach { navigator.popTop() }
        .launchIn(this)

    suspend fun updatePermissions() {
        val permissions = request.permissions
            .filterNot { hasPermissions(listOf(it)).first() }
        reduce { copy(permissions = permissions) }
    }

    launch { updatePermissions() }

    actions
        .filterIsInstance<RequestPermission>()
        .onEach { action ->
            action.permission.requestHandler().request(action.permission)
            startUi()
            updatePermissions()
        }
        .launchIn(this)
}

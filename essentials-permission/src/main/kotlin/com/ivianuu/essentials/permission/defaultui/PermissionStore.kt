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
import com.ivianuu.essentials.store.iterator
import com.ivianuu.essentials.store.reduce
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.popTop
import com.ivianuu.essentials.ui.store.Initial
import com.ivianuu.essentials.ui.store.UiStoreBinding
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.startUi
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@UiStoreBinding
fun PermissionStore(
    hasPermissions: hasPermissions,
    initial: @Initial PermissionState = PermissionState(),
    logger: Logger,
    navigator: Navigator,
    request: PermissionRequest,
    requestHandler: requestHandler,
    scope: CoroutineScope,
    startUi: startUi
) = scope.store<PermissionState, PermissionAction>(initial) {
    suspend fun updatePermissionsToProcessOrFinish() {
        val permissionsToProcess = request.permissions
            .filterNot { hasPermissions(listOf(it)).first() }

        logger.d("update permissions to process or finish not granted $permissionsToProcess")

        if (permissionsToProcess.isEmpty()) {
            navigator.popTop()
        } else {
            reduce { copy(permissionsToProcess = permissionsToProcess) }
        }
    }

    launch { updatePermissionsToProcessOrFinish() }

    for (action in this) {
        when (action) {
            is RequestPermission -> {
                action.permission.requestHandler().request(action.permission)
                startUi()
                updatePermissionsToProcessOrFinish()
            }
        }
    }
}

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

package com.ivianuu.essentials.permission.defaultui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.foundation.Text
import androidx.compose.material.Button
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.GivenPermissionRequestRouteFactory
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequest
import com.ivianuu.essentials.permission.PermissionRequestRouteFactory
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.hasPermissions
import com.ivianuu.essentials.permission.requestHandler
import com.ivianuu.essentials.store.onEachAction
import com.ivianuu.essentials.store.setState
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.store.component1
import com.ivianuu.essentials.ui.store.component2
import com.ivianuu.essentials.ui.store.rememberStore
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.essentials.util.startUi
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.flow.first

@GivenPermissionRequestRouteFactory
internal class DefaultPermissionRequestRouteFactory : PermissionRequestRouteFactory {

    override fun createRoute(request: PermissionRequest): Route =
        Route { DefaultPermissionPage(request) }

}

@Reader
@Composable
private fun DefaultPermissionPage(request: PermissionRequest) {
    val (state, dispatch) = rememberStore {
        defaultPermissionStore(request)
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Required Permissions") }) // todo customizable and/or res
        }
    ) {
        InsettingScrollableColumn {
            state.permissionsToProcess.forEach { permission ->
                Permission(
                    permission = permission,
                    onClick = { dispatch(PermissionAction.PermissionClicked(permission)) }
                )
            }
        }
    }
}

@Composable
private fun Permission(
    onClick: () -> Unit,
    permission: Permission
) {
    key(permission) {
        ListItem(
            title = { Text(permission[Permission.Title]) },
            subtitle = permission.getOrNull(Permission.Desc)?.let {
                {
                    Text(it)
                }
            },
            leading = permission.getOrNull(Permission.Icon),
            trailing = {
                Button(onClick = onClick) { Text("GRANT") } // todo res
            },
            onClick = onClick
        )
    }
}

@Reader
private fun defaultPermissionStore(
    request: PermissionRequest
) = store<PermissionState, PermissionAction>(
    PermissionState()
) {
    suspend fun updatePermissionsToProcessOrFinish() {
        val permissionsToProcess = request.permissions
            .filterNot { hasPermissions(it).first() }

        d { "update permissions to process or finish not granted $permissionsToProcess" }

        if (permissionsToProcess.isEmpty()) {
            navigator.popTop()
        } else {
            setState { copy(permissionsToProcess = permissionsToProcess) }
        }
    }

    updatePermissionsToProcessOrFinish()

    onEachAction { action ->
        when (action) {
            is PermissionAction.PermissionClicked -> {
                action.permission.requestHandler.request(action.permission)
                startUi()
                updatePermissionsToProcessOrFinish()
            }
        }.exhaustive
    }
}


@Immutable
private data class PermissionState(
    val permissionsToProcess: List<Permission> = emptyList()
)

private sealed class PermissionAction {
    data class PermissionClicked(val permission: Permission) : PermissionAction()
}

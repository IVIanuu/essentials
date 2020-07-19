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

import androidx.compose.Composable
import androidx.compose.frames.modelListOf
import androidx.compose.key
import androidx.compose.mutableStateListOf
import androidx.ui.foundation.Text
import androidx.ui.material.Button
import com.ivianuu.essentials.permission.BindPermissionRequestRouteFactory
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequest
import com.ivianuu.essentials.permission.PermissionRequestHandlers
import com.ivianuu.essentials.permission.PermissionRequestRouteFactory
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.viewmodel.ViewModel
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.essentials.util.startUi
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@BindPermissionRequestRouteFactory
@Given
internal class DefaultPermissionRequestRouteFactory : PermissionRequestRouteFactory {

    override fun createRoute(request: PermissionRequest): Route =
        Route { DefaultPermissionPage(request) }

}

@Reader
@Composable
internal fun DefaultPermissionPage(request: PermissionRequest) {
    val viewModel = viewModel { given<DefaultPermissionViewModel>(request) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Required Permissions") }) // todo customizable and/or res
        }
    ) {
        InsettingScrollableColumn {
            viewModel.permissionsToProcess.forEach { permission ->
                Permission(
                    permission = permission,
                    onClick = { viewModel.permissionClicked(permission) }
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

@Given
internal class DefaultPermissionViewModel(
    private val request: PermissionRequest
) : ViewModel() {

    private val _permissionsToProcess = mutableStateListOf<Permission>()
    val permissionsToProcess: List<Permission> get() = _permissionsToProcess

    init {
        updatePermissionsToProcessOrFinish()
    }

    fun permissionClicked(permission: Permission) {
        scope.launch {
            given<PermissionRequestHandlers>()
                .requestHandlerFor(permission)
                .request(permission)
            startUi()
            updatePermissionsToProcessOrFinish()
        }
    }

    private fun updatePermissionsToProcessOrFinish() {
        scope.launch {
            val permissionsToProcess = request.permissions
                .filterNot { given<PermissionManager>().hasPermissions(it).first() }

            d { "update permissions to process or finish not granted $permissionsToProcess" }

            if (permissionsToProcess.isEmpty()) {
                navigator.popTop()
            } else {
                withContext(dispatchers.main) { // todo remove
                    _permissionsToProcess.clear()
                    _permissionsToProcess += permissionsToProcess
                }
            }
        }
    }

}

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

package com.ivianuu.essentials.permission.dialogui

import androidx.compose.Composable
import androidx.compose.frames.modelListOf
import androidx.compose.key
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.material.TextButton
import com.ivianuu.essentials.permission.BindPermissionRequestRouteFactory
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequest
import com.ivianuu.essentials.permission.PermissionRequestHandlers
import com.ivianuu.essentials.permission.PermissionRequestRouteFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.DialogRoute
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.RouteAmbient
import com.ivianuu.essentials.ui.viewmodel.ViewModel
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.StartUi
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@BindPermissionRequestRouteFactory
@Transient
internal class DialogPermissionRequestRouteFactory(
    private val dialog: PermissionDialog
) : PermissionRequestRouteFactory {

    override fun createRoute(request: PermissionRequest): Route {
        return DialogRoute {
            dialog(request)
        }
    }

}

@Transient
internal class PermissionDialog(
    private val viewModelFactory: @Provider (PermissionRequest, Route) -> PermissionDialogViewModel
) {

    @Composable
    operator fun invoke(request: PermissionRequest) {
        val route = RouteAmbient.current
        val viewModel = viewModel { viewModelFactory(request, route) }

        Dialog(
            title = { Text("Required Permissions") }, // todo customizable
            content = {
                VerticalScroller {
                    viewModel.permissionsToProcess.forEach { permission ->
                        Permission(
                            permission = permission,
                            onClick = { viewModel.permissionClicked(permission) }
                        )
                    }
                }
            },
            applyContentPadding = false,
            negativeButton = {
                TextButton(onClick = { viewModel.cancelClicked() }) {
                    Text(R.string.es_cancel)
                }
            }
        )
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
                onClick = onClick
            )
        }
    }

}

@Transient
internal class PermissionDialogViewModel(
    private val logger: Logger,
    private val manager: PermissionManager,
    private val navigator: Navigator,
    private val request: @Assisted PermissionRequest,
    private val requestHandlers: PermissionRequestHandlers,
    private val route: @Assisted Route,
    private val startUi: StartUi
) : ViewModel() {

    private val _permissionsToProcess = modelListOf<Permission>()
    val permissionsToProcess: List<Permission> get() = _permissionsToProcess

    init {
        updatePermissionsToProcessOrFinish()
    }

    fun permissionClicked(permission: Permission) {
        scope.launch {
            requestHandlers.requestHandlerFor(permission).request(permission)
            startUi()
            updatePermissionsToProcessOrFinish()
        }
    }

    fun cancelClicked() {
        navigator.pop(route = route)
        request.onComplete.complete(Unit)
    }

    private fun updatePermissionsToProcessOrFinish() {
        scope.launch {
            val permissionsToProcess = request.permissions
                .filterNot { manager.hasPermissions(it).first() }

            logger.d("update permissions to process or finish not granted $permissionsToProcess")

            if (permissionsToProcess.isEmpty()) {
                navigator.pop(route = route)
                request.onComplete.complete(Unit)
            } else {
                _permissionsToProcess.clear()
                _permissionsToProcess += permissionsToProcess
            }
        }
    }

}

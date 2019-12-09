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
import androidx.compose.Recompose
import androidx.compose.frames.modelListOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import androidx.ui.core.Text
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.MetadataKeys
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionActivity
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequest
import com.ivianuu.essentials.permission.PermissionRequestUi
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.ui.base.EsViewModel
import com.ivianuu.essentials.ui.compose.core.ActivityAmbient
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.dialog.DialogButton
import com.ivianuu.essentials.ui.compose.dialog.ScrollableDialog
import com.ivianuu.essentials.ui.compose.dialog.dialogRoute
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.compose.resources.stringResource
import com.ivianuu.essentials.ui.compose.viewmodel.injekt.injectViewModel
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.launch

@Factory
class DialogPermissionRequestUi(
    private val navigator: Navigator
) : PermissionRequestUi {

    override fun performRequest(
        activity: FragmentActivity,
        manager: PermissionManager,
        request: PermissionRequest
    ) {
        navigator.push(
            dialogRoute {
                PermissionDialog(request = request)
            }
        )
    }

}

@Composable
private fun PermissionDialog(request: PermissionRequest) = composable {
    Recompose { recompose ->
        ScrollableDialog(
            title = { Text("Permission") }, // todo
            listContent = {
                val viewModel = injectViewModel<PermissionDialogViewModel> {
                    parametersOf(request)
                }
                val activity = ambient(ActivityAmbient) as PermissionActivity
                viewModel.permissionsToProcess.forEach { permission ->
                    Permission(
                        permission = permission,
                        onClick = { viewModel.permissionClicked(activity, permission) }
                    )
                }
            },
            negativeButton = {
                DialogButton(text = stringResource(R.string.es_cancel))
            }
        )
    }
}

@Composable
private fun Permission(
    permission: Permission,
    onClick: () -> Unit
) = composableWithKey(permission) {
    SimpleListItem(
        title = permission.metadata[MetadataKeys.Title],
        subtitle = permission.metadata.getOrNull(MetadataKeys.Desc),
        image = permission.metadata.getOrNull(MetadataKeys.Icon),
        onClick = onClick
    )
}

@Factory
class PermissionDialogViewModel(
    private val manager: PermissionManager,
    @Param private val request: PermissionRequest
) : EsViewModel() {

    private val _permissionsToProcess = modelListOf<Permission>()
    val permissionsToProcess: List<Permission> get() = _permissionsToProcess

    init {
        updatePermissionsToProcessOrFinish()
    }

    fun permissionClicked(activity: PermissionActivity, permission: Permission) {
        viewModelScope.launch {
            d { "request $permission" }
            manager.requestHandlerFor(permission)
                .request(activity, manager, permission)

            updatePermissionsToProcessOrFinish()
        }
    }

    private fun updatePermissionsToProcessOrFinish() {
        viewModelScope.launch {
            val permissionsToProcess = request.permissions
                .filterNot { manager.hasPermissions(it) }

            d { "update permissions to process or finish not granted $permissionsToProcess" }

            if (permissionsToProcess.isEmpty()) {
                request.onComplete()
            } else {
                _permissionsToProcess.clear()
                _permissionsToProcess += permissionsToProcess
            }
        }
    }
}
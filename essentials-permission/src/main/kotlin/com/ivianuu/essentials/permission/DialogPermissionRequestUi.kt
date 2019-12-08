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

import androidx.compose.Composable
import androidx.compose.Recompose
import androidx.compose.frames.modelListOf
import androidx.lifecycle.viewModelScope
import androidx.ui.core.Text
import com.ivianuu.essentials.ui.base.EsViewModel
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.dialog.DialogButton
import com.ivianuu.essentials.ui.compose.dialog.ScrollableDialog
import com.ivianuu.essentials.ui.compose.dialog.dialogRoute
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.compose.resources.stringResource
import com.ivianuu.essentials.ui.compose.viewmodel.injekt.injectViewModel
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import kotlinx.coroutines.launch

@Factory
class DialogPermissionRequestUi : PermissionRequestUi {

    override fun performRequest(activity: PermissionActivity, request: PermissionRequest) {
        activity.navigator.push(
            dialogRoute {
                PermissionDialog(activity = activity, request = request)
            }
        )
    }

}

@Composable
private fun PermissionDialog(
    activity: PermissionActivity,
    request: PermissionRequest
) = composable {
    Recompose { recompose ->
        ScrollableDialog(
            title = { Text("Permission") }, // todo
            listContent = {
                val viewModel = injectViewModel<PermissionDialogViewModel>()
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
) = composableWithKey(permission.key) {
    SimpleListItem(
        title = permission.metadata.title,
        subtitle = permission.metadata.desc,
        // todo image = drawableResource(permission.iconRes),
        onClick = onClick
    )
}

@Factory
class PermissionDialogViewModel(
    @Param private val request: PermissionRequest
) : EsViewModel() {

    private val _permissionsToProcess = modelListOf<Permission>()
    val permissionsToProcess: List<Permission> get() = _permissionsToProcess

    init {
        updatePermissionsToProcessOrFinish()
    }

    fun permissionClicked(activity: PermissionActivity, permission: Permission) {
        viewModelScope.launch {
            permission.requestHandler.request(activity, permission)
            updatePermissionsToProcessOrFinish()
        }
    }

    private fun updatePermissionsToProcessOrFinish() {
        viewModelScope.launch {
            val permissionsToProcess = request.permissions
                .filterNot { it.stateProvider.isGranted() }

            if (permissionsToProcess.isEmpty()) {
                request.onComplete()
            } else {
                _permissionsToProcess.clear()
                _permissionsToProcess += permissionsToProcess
            }
        }
    }
}
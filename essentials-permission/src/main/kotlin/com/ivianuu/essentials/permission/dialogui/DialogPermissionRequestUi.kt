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
import androidx.fragment.app.FragmentActivity
import androidx.ui.foundation.VerticalScroller
import com.ivianuu.essentials.permission.BindPermissionRequestUi
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionActivity
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequest
import com.ivianuu.essentials.permission.PermissionRequestHandlers
import com.ivianuu.essentials.permission.PermissionRequestUi
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.ui.base.ViewModel
import com.ivianuu.essentials.ui.common.RetainedScrollerPosition
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogButton
import com.ivianuu.essentials.ui.dialog.DialogRoute
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.composition.get
import kotlinx.coroutines.launch

@BindPermissionRequestUi
@Transient
internal class DialogPermissionRequestUi : PermissionRequestUi {

    override fun performRequest(
        activity: FragmentActivity,
        manager: PermissionManager,
        request: PermissionRequest
    ) {
        activity.activityComponent.get<Navigator>()
            .push(PermissionRoute(request))
    }
}

private fun PermissionRoute(request: PermissionRequest) = DialogRoute(
    dismissHandler = { compositionActivity.finish() }
) {
    Dialog(
        title = { Text("Required Permissions") }, // todo customizable
        content = {
            VerticalScroller(
                scrollerPosition = RetainedScrollerPosition()
            ) {
                val viewModelFactory =
                    inject<@Provider (PermissionRequest) -> PermissionDialogViewModel>()
                val viewModel = viewModel { viewModelFactory(request) }
                val activity = compositionActivity as PermissionActivity
                viewModel.permissionsToProcess.forEach { permission ->
                    Permission(
                        permission = permission,
                        onClick = { viewModel.permissionClicked(activity, permission) }
                    )
                }
            }
        },
        applyContentPadding = false,
        negativeButton = {
            val activity = compositionActivity
            DialogButton(onClick = { activity.finish() }) {
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
            title = { Text(permission.metadata[Metadata.Title]) },
            subtitle = permission.metadata.getOrNull(Metadata.Desc)?.let {
                {
                    Text(it)
                }
            },
            leading = permission.metadata.getOrNull(Metadata.Icon),
            onClick = onClick
        )
    }
}

@Transient
internal class PermissionDialogViewModel(
    private val logger: Logger,
    private val manager: PermissionManager,
    private val request: @Assisted PermissionRequest,
    private val requestHandlers: PermissionRequestHandlers
) : ViewModel() {

    private val _permissionsToProcess = modelListOf<Permission>()
    val permissionsToProcess: List<Permission> get() = _permissionsToProcess

    init {
        updatePermissionsToProcessOrFinish()
    }

    fun permissionClicked(activity: PermissionActivity, permission: Permission) {
        coroutineScope.launch {
            logger.d("request $permission")
            requestHandlers.requestHandlerFor(permission)
                .request(activity, manager, permission)
            updatePermissionsToProcessOrFinish()
        }
    }

    private fun updatePermissionsToProcessOrFinish() {
        coroutineScope.launch {
            val permissionsToProcess = request.permissions
                .filterNot { manager.hasPermissions(it) }

            logger.d("update permissions to process or finish not granted $permissionsToProcess")

            if (permissionsToProcess.isEmpty()) {
                request.onComplete()
            } else {
                _permissionsToProcess.clear()
                _permissionsToProcess += permissionsToProcess
            }
        }
    }
}

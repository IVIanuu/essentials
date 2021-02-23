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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.ui.PermissionRequestAction.RequestPermission
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.core.localVerticalInsets
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.injekt.Given

@KeyUiBinding<PermissionRequestKey>
@Given
fun permissionRequestUiKeyUi(
    @Given stateProvider: @Composable () -> @UiState PermissionRequestState,
    @Given dispatch: DispatchAction<PermissionRequestAction>,
): KeyUi = {
    val state = stateProvider()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Required Permissions") }) // todo customizable and/or res
        }
    ) {
        LazyColumn(contentPadding = localVerticalInsets()) {
            items(state.permissions) { permission ->
                Permission(
                    permission = permission.permission,
                    onClick = { dispatch(RequestPermission(permission)) }
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
    ListItem(
        title = { Text(permission.title) },
        subtitle = permission.desc?.let {
            {
                Text(it)
            }
        },
        leading = permission.icon,
        trailing = {
            Button(onClick = onClick) { Text("GRANT") } // todo res
        },
        onClick = onClick
    )
}

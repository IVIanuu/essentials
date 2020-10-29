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

import androidx.compose.foundation.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequest
import com.ivianuu.essentials.permission.PermissionRequestRouteFactory
import com.ivianuu.essentials.permission.PermissionRequestRouteFactoryBinding
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.hasPermissions
import com.ivianuu.essentials.permission.requestHandler
import com.ivianuu.essentials.store.storeProvider
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.popTop
import com.ivianuu.essentials.ui.store.component1
import com.ivianuu.essentials.ui.store.component2
import com.ivianuu.essentials.ui.store.rememberStore1
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.essentials.util.startUi
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.first

@FunBinding
@Composable
fun PermissionPage(
    store: rememberStore1<PermissionState, PermissionAction, PermissionRequest>,
    request: @Assisted PermissionRequest
) {
    val (state, dispatch) = store(request)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Required Permissions") }) // todo customizable and/or res
        }
    ) {
        InsettingScrollableColumn {
            state.permissionsToProcess.forEach { permission ->
                Permission(
                    permission = permission,
                    onClick = { dispatch(PermissionAction.RequestPermission(permission)) }
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

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
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.ui.PermissionRequestAction.GrantPermission
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.onAction
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take

class PermissionRequestKey(val permissionsKeys: List<TypeKey<Permission>>) : Key<Boolean>

@Given
val permissionRequestUi: StoreKeyUi<PermissionRequestKey, PermissionRequestState,
        PermissionRequestAction> = {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Required permissions") }) // todo customizable and/or res
        }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            items(state.permissions) { permission ->
                ListItem(
                    title = { Text(permission.permission.title) },
                    subtitle = permission.permission.desc?.let {
                        {
                            Text(it)
                        }
                    },
                    leading = permission.permission.icon,
                    trailing = {
                        Button(onClick = { send(GrantPermission(permission)) }) {
                            Text("GRANT") // todo res
                        }
                    },
                    onClick = { send(GrantPermission(permission)) }
                )
            }
        }
    }
}

data class PermissionRequestState(val permissions: List<UiPermission<*>> = emptyList())

data class UiPermission<P : Permission>(
    val permissionKey: TypeKey<P>,
    val permission: P
)

sealed class PermissionRequestAction {
    data class GrantPermission(val permission: UiPermission<*>) : PermissionRequestAction()
}

@Given
fun permissionRequestStore(
    @Given appUiStarter: AppUiStarter,
    @Given key: PermissionRequestKey,
    @Given navigator: Navigator,
    @Given permissions: Map<TypeKey<Permission>, Permission>,
    @Given permissionStateFactory: PermissionStateFactory,
    @Given requestHandlers: Map<TypeKey<Permission>, PermissionRequestHandler<Permission>>
): StoreBuilder<KeyUiGivenScope, PermissionRequestState, PermissionRequestAction> = {
    state
        .filter {
            key.permissionsKeys
                .all { permissionStateFactory(listOf(it)).first() }
        }
        .take(1)
        .effect { navigator.pop(key, true) }

    suspend fun updatePermissions() {
        val notGrantedPermissions = key.permissionsKeys
            .filterNot { permissionStateFactory(listOf(it)).first() }
            .map { UiPermission(it, permissions[it]!!) }
        update { copy(permissions = notGrantedPermissions) }
    }

    effect { updatePermissions() }

    onAction<GrantPermission> {
        requestHandlers[it.permission.permissionKey]!!(permissions[it.permission.permissionKey]!!)
        appUiStarter()
        updatePermissions()
    }
}

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.ui.PermissionRequestAction.RequestPermission
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.core.localVerticalInsets
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

data class PermissionRequestKey(val permissionsKeys: List<TypeKey<Permission>>) : Key<Boolean>

@Module
val permissionRequestKeyModule = KeyModule<PermissionRequestKey>()

@Given
fun permissionRequestUiUi(
    @Given stateFlow: StateFlow<PermissionRequestState>,
    @Given dispatch: Collector<PermissionRequestAction>,
): KeyUi<PermissionRequestKey> = {
    val state by stateFlow.collectAsState()
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

data class PermissionRequestState(val permissions: List<UiPermission<*>> = emptyList())

data class UiPermission<P : Permission>(
    val permissionKey: TypeKey<P>,
    val permission: P
)

sealed class PermissionRequestAction {
    data class RequestPermission(val permission: UiPermission<*>) : PermissionRequestAction()
}


@Scoped<KeyUiComponent>
@Given
fun permissionRequestState(
    @Given scope: KeyUiScope,
    @Given initial: @Initial PermissionRequestState = PermissionRequestState(),
    @Given actions: Flow<PermissionRequestAction>,
    @Given appUiStarter: AppUiStarter,
    @Given key: PermissionRequestKey,
    @Given navigator: Collector<NavigationAction>,
    @Given permissions: Map<TypeKey<Permission>, Permission>,
    @Given permissionStateFactory: PermissionStateFactory,
    @Given requestHandlers: Map<TypeKey<Permission>, PermissionRequestHandler<Permission>>
): StateFlow<PermissionRequestState> = scope.state(initial) {
    state
        .filter {
            key.permissionsKeys
                .all { permissionStateFactory(listOf(it)).first() }
        }
        .take(1)
        .onEach { navigator(NavigationAction.Pop(key, true)) }
        .launchIn(this)

    suspend fun updatePermissions() {
        val permissions = key.permissionsKeys
            .filterNot { permissionStateFactory(listOf(it)).first() }
            .map { UiPermission(it, permissions[it]!!) }
        reduce { copy(permissions = permissions) }
    }

    launch { updatePermissions() }

    actions
        .filterIsInstance<RequestPermission>()
        .onEach { action ->
            requestHandlers[action.permission.permissionKey]!!(permissions[action.permission.permissionKey]!!)
            appUiStarter()
            updatePermissions()
        }
        .launchIn(this)
}

@Scoped<KeyUiComponent>
@Given
val permissionRequestActions get() = EventFlow<PermissionRequestAction>()

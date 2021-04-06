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
import com.ivianuu.essentials.coroutines.collectAsEffectIn
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.TmpStateKeyUi
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take

class PermissionRequestKey(val permissionsKeys: List<TypeKey<Permission>>) : Key<Boolean>

@Given
val permissionRequestUi: TmpStateKeyUi<PermissionRequestKey, PermissionRequestViewModel,
        PermissionRequestState> = { viewModel, state ->
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
                        Button(onClick = {
                            viewModel.grantPermission(permission)
                        }) { Text("GRANT") } // todo res
                    },
                    onClick = { viewModel.grantPermission(permission) }
                )
            }
        }
    }
}

data class PermissionRequestState(val permissions: List<UiPermission<*>> = emptyList()) : State()

data class UiPermission<P : Permission>(
    val permissionKey: TypeKey<P>,
    val permission: P
)

@Scoped<KeyUiGivenScope>
@Given
class PermissionRequestViewModel(
    @Given private val appUiStarter: AppUiStarter,
    @Given private val key: PermissionRequestKey,
    @Given private val navigator: Navigator,
    @Given private val permissions: Map<TypeKey<Permission>, Permission>,
    @Given private val permissionStateFactory: PermissionStateFactory,
    @Given private val requestHandlers: Map<TypeKey<Permission>, PermissionRequestHandler<Permission>>,
    @Given private val store: ScopeStateStore<KeyUiGivenScope, PermissionRequestState>
) : StateFlow<PermissionRequestState> by store {
    init {
        store
            .filter {
                key.permissionsKeys
                    .all { permissionStateFactory(listOf(it)).first() }
            }
            .take(1)
            .collectAsEffectIn(store) { navigator.pop(key, true) }
        store.effect { updatePermissions() }
    }

    fun grantPermission(permission: UiPermission<*>) = store.effect {
        requestHandlers[permission.permissionKey]!!(permissions[permission.permissionKey]!!)
        appUiStarter()
        updatePermissions()
    }

    private suspend fun updatePermissions() {
        val permissions = key.permissionsKeys
            .filterNot { permissionStateFactory(listOf(it)).first() }
            .map { UiPermission(it, permissions[it]!!) }
        store.update { copy(permissions = permissions) }
    }
}

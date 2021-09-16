/*
 * Copyright 2021 Manuel Wrage
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

import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

data class PermissionRequestKey(val permissionsKeys: List<TypeKey<Permission>>) : Key<Boolean>

@Provide val permissionRequestUi: ModelKeyUi<PermissionRequestKey, PermissionRequestModel> = {
  SimpleListScreen(R.string.es_request_permission_title) {
    items(model.permissions) { permission ->
      ListItem(
        title = { Text(permission.permission.title) },
        subtitle = permission.permission.desc?.let {
          {
            Text(it)
          }
        },
        leading = permission.permission.icon,
        trailing = {
          Button(onClick = { model.grantPermission(permission) }) {
            Text(R.string.es_grant)
          }
        },
        onClick = { model.grantPermission(permission) }
      )
    }
  }
}

@Optics data class PermissionRequestModel(
  val permissions: List<UiPermission<*>> = emptyList(),
  val grantPermission: (UiPermission<*>) -> Unit = {}
)

data class UiPermission<P : Permission>(
  val permissionKey: TypeKey<P>,
  val permission: P
)

@Provide fun permissionRequestModel(
  appUiStarter: AppUiStarter,
  key: PermissionRequestKey,
  navigator: Navigator,
  permissions: Map<TypeKey<Permission>, Permission> = emptyMap(),
  permissionStateFactory: PermissionStateFactory,
  requestHandlers: Map<TypeKey<Permission>, PermissionRequestHandler<Permission>> = emptyMap(),
  scope: NamedCoroutineScope<KeyUiScope>
): @Scoped<KeyUiScope> StateFlow<PermissionRequestModel> =
  scope.state(PermissionRequestModel()) {
    state
      .filter {
        key.permissionsKeys
          .all { permissionStateFactory(listOf(it)).first() }
      }
      .take(1)
      .onEach { navigator.pop(key, true) }
      .launchIn(this)

    suspend fun updatePermissions() {
      val notGrantedPermissions = key.permissionsKeys
        .filterNot { permissionStateFactory(listOf(it)).first() }
        .map { UiPermission(it, permissions[it]!!) }
      update { copy(permissions = notGrantedPermissions) }
    }

    launch { updatePermissions() }

    action(PermissionRequestModel.grantPermission()) { permission ->
      requestHandlers[permission.permissionKey]!!(permissions[permission.permissionKey]!!)
      appUiStarter()
      updatePermissions()
    }
  }

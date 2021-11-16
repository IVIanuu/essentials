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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.valueFromFlow
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.first

data class PermissionRequestKey(val permissionsKeys: List<TypeKey<Permission>>) : Key<Boolean>

@Provide val permissionRequestUi: ModelKeyUi<PermissionRequestKey, PermissionRequestModel> = {
  SimpleListScreen(R.string.es_request_permission_title) {
    items(model.permissions) { permission ->
      ListItem(
        modifier = Modifier.clickable { model.grantPermission(permission) },
        title = { Text(permission.permission.title) },
        subtitle = permission.permission.desc?.let {
          {
            Text(it)
          }
        },
        leading = { permission.permission.Icon() },
        trailing = { Switch(checked = permission.isGranted, null) }
      )
    }
  }
}

data class PermissionRequestModel(
  val permissions: List<UiPermission<*>>,
  val grantPermission: (UiPermission<*>) -> Unit
)

data class UiPermission<P : Permission>(
  val permissionKey: TypeKey<P>,
  val permission: P,
  val isGranted: Boolean
)

@Provide @Composable fun permissionRequestModel(
  appUiStarter: AppUiStarter,
  permissions: Map<TypeKey<Permission>, Permission> = emptyMap(),
  permissionStateFactory: PermissionStateFactory,
  requestHandlers: Map<TypeKey<Permission>, PermissionRequestHandler<Permission>> = emptyMap(),
  ctx: KeyUiContext<PermissionRequestKey>
): PermissionRequestModel {
  val model = PermissionRequestModel(
    permissions = ctx.key.permissionsKeys
      .map { permissionKey ->
        UiPermission(
          permissionKey,
          permissions[permissionKey]!!,
          valueFromFlow(false) { permissionStateFactory(listOf(permissionKey)) }
        )
      },
    grantPermission = action { permission ->
      requestHandlers[permission.permissionKey]!!(permissions[permission.permissionKey]!!)
      appUiStarter()
    }
  )

  LaunchedEffect(model) {
    if (ctx.key.permissionsKeys
        .all { permissionStateFactory(listOf(it)).first() }) {
      ctx.navigator.pop(ctx.key, true)
    }
  }

  return model
}

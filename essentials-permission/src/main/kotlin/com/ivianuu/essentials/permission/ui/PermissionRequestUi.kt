/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

data class PermissionRequestKey(val permissionsKeys: List<TypeKey<Permission>>) : Key<Boolean>

@Provide val permissionRequestUi = ModelKeyUi<PermissionRequestKey, PermissionRequestModel> {
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

@Provide fun permissionRequestModel(
  appUiStarter: AppUiStarter,
  permissions: Map<TypeKey<Permission>, Permission>,
  permissionStateFactory: PermissionStateFactory,
  requestHandlers: Map<TypeKey<Permission>, PermissionRequestHandler<Permission>>,
  ctx: KeyUiContext<PermissionRequestKey>
): @Composable () -> PermissionRequestModel = {
  val model = PermissionRequestModel(
    permissions = ctx.key.permissionsKeys
      .map { permissionKey ->
        key(permissionKey.value) {
          UiPermission(
            permissionKey,
            permissions[permissionKey]!!,
            permissionStateFactory(listOf(permissionKey)).bind(false)
          )
        }
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

  model
}

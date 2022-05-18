/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.analytics.Analytics
import com.ivianuu.essentials.analytics.log
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.bind
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.navigation.CriticalUserFlowKey
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.first

data class PermissionRequestKey(
  val permissionsKeys: List<TypeKey<Permission>>
) : CriticalUserFlowKey<Boolean>

@Provide val permissionRequestUi = ModelKeyUi<PermissionRequestKey, PermissionRequestModel> {
  SimpleListScreen(R.string.es_request_permission_title) {
    items(permissions) { permission ->
      ListItem(
        modifier = Modifier.clickable { grantPermission(permission) },
        title = { Text(permission.permission.title) },
        subtitle = permission.permission.desc?.let {
          {
            Text(it)
          }
        },
        leading = {
          Box(modifier = Modifier.size(40.dp)) {
            permission.permission.icon?.invoke()
          }
        },
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
  analytics: Analytics,
  appUiStarter: AppUiStarter,
  permissions: Map<TypeKey<Permission>, Permission>,
  permissionStateFactory: PermissionStateFactory,
  requestHandlers: Map<TypeKey<Permission>, PermissionRequestHandler<Permission>>,
  ctx: KeyUiContext<PermissionRequestKey>
) = Model {
  LaunchedEffect(true) {
    permissionStateFactory(ctx.key.permissionsKeys)
      .first { it }
    ctx.navigator.pop(ctx.key, true)
  }

  PermissionRequestModel(
    permissions = ctx.key.permissionsKeys
      .map { permissionKey ->
        UiPermission(
          permissionKey,
          permissions[permissionKey]!!,
          permissionStateFactory(listOf(permissionKey)).bind(false)
        )
      },
    grantPermission = action { permission ->
      val state = permissionStateFactory(listOf(permission.permissionKey))
      if (!state.first()) {
        requestHandlers[permission.permissionKey]!!(permissions[permission.permissionKey]!!)
        analytics.log("permission_requested") {
          put("key", permission.permissionKey.value)
          put("granted", state.first().toString())
        }
        appUiStarter()
      }
    }
  )
}

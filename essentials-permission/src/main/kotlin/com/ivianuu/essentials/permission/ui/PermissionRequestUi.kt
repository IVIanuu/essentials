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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.navigation.CriticalUserFlowScreen
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.first

class PermissionRequestScreen(
  val permissionsKeys: List<TypeKey<Permission>>
) : CriticalUserFlowScreen<Boolean>

@Provide val permissionRequestUi = Ui<PermissionRequestScreen, PermissionRequestModel> { model ->
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
  appUiStarter: AppUiStarter,
  navigator: Navigator,
  permissionManager: PermissionManager,
  requestHandlers: Map<TypeKey<Permission>, () -> PermissionRequestHandler<Permission>>,
  screen: PermissionRequestScreen
) = Model {
  LaunchedEffect(true) {
    permissionManager.permissionState(screen.permissionsKeys)
      .first { it }
    navigator.pop(screen, true)
  }

  PermissionRequestModel(
    permissions = screen.permissionsKeys
      .map { permissionKey ->
        UiPermission(
          permissionKey,
          permissionManager.permission(permissionKey),
          permissionManager.permissionState(listOf(permissionKey)).collectAsState(false).value
        )
      },
    grantPermission = action { permission ->
      requestHandlers[permission.permissionKey]!!()(permission.permission)
      appUiStarter()
    }
  )
}

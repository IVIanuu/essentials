/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.navigation.AppUiStarter
import com.ivianuu.essentials.ui.navigation.CriticalUserFlowScreen
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.first

class PermissionRequestScreen(
  val permissionsKeys: List<TypeKey<Permission>>
) : CriticalUserFlowScreen<Boolean>

@Provide val permissionRequestUi = Ui<PermissionRequestScreen, PermissionRequestModel> { model ->
  Scaffold(topBar = { AppBar { Text(R.string.es_request_permission_title) } }) {
    VerticalList {
      items(model.permissionsToGrant) { permission ->
        ListItem(
          modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp)
            .border(
              1.dp,
              LocalContentColor.current.copy(alpha = 0.12f),
              RoundedCornerShape(8.dp)
            ),
          contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
          textPadding = PaddingValues(start = 16.dp),
          title = { Text(permission.title) },
          subtitle = permission.desc?.let { { Text(it) } },
          leading = { permission.icon?.invoke() },
          trailing = {
            Row(horizontalArrangement = Arrangement.End) {
              TextButton(
                modifier = Modifier.width(56.dp),
                onClick = { model.denyPermission(permission) }
              ) {
                Text(R.string.es_deny, maxLines = 1)
              }

              TextButton(
                modifier = Modifier.width(56.dp),
                onClick = { model.grantPermission(permission) }
              ) {
                Text(R.string.es_grant, maxLines = 1)
              }
            }
          }
        )
      }
    }
  }
}

data class PermissionRequestModel(
  val permissionsToGrant: List<Permission>,
  val grantPermission: (Permission) -> Unit,
  val denyPermission: (Permission) -> Unit
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

  val keysByPermission = remember {
    screen.permissionsKeys.associateBy { permissionManager.permission(it) }
  }

  PermissionRequestModel(
    permissionsToGrant = keysByPermission
      .keys
      .filterNot {
        key(keysByPermission[it]) {
          remember { permissionManager.permissionState(listOf(keysByPermission[it]!!)) }
            .collectAsState(false).value
        }
      },
    grantPermission = action { permission ->
      requestHandlers[keysByPermission[permission]!!]!!()(permission)
      appUiStarter()
    },
    denyPermission = action { _ -> navigator.pop(screen, false) }
  )
}

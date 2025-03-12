/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

class PermissionRequestScreen(
  val permissionsKeys: List<KClass<out Permission>>
) : CriticalUserFlowScreen<Boolean>

@Provide @Composable fun PermissionRequestUi(
  appUiStarter: AppUiStarter,
  navigator: Navigator,
  permissionManager: PermissionManager,
  requestHandlers: Map<KClass<out Permission>, () -> PermissionRequestHandler<Permission>>,
  screen: PermissionRequestScreen
): Ui<PermissionRequestScreen> {
  LaunchedScopedEffect(true) {
    permissionManager.permissionState(screen.permissionsKeys).first { it }
    navigator.pop(screen, true)
  }

  val keysByPermission = remember {
    screen.permissionsKeys.associateBy { permissionManager.permission(it) }
  }

  val permissionsToGrant = keysByPermission
    .keys
    .filterNot { permission ->
      key(permission) {
        produceScopedState(false) {
          permissionManager.permissionState(listOf(keysByPermission[permission]!!))
            .collect { value = it }
        }
          .value
      }
    }

  EsScaffold(topBar = { EsAppBar { Text("Required permissions") } }) {
    EsLazyColumn {
      items(permissionsToGrant, { it }) { permission ->
        EsListItem(
          modifier = Modifier
            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
            .border(
              1.dp,
              LocalContentColor.current.copy(alpha = 0.12f),
              RoundedCornerShape(8.dp)
            )
            .animateItem(),
          headlineContent = { Text(permission.title) },
          supportingContent = permission.desc?.let { { Text(it) } },
          leadingContent = { permission.icon?.invoke() },
          trailingContent = {
            Row(horizontalArrangement = Arrangement.End) {
              TextButton(
                onClick = action { navigator.pop(screen, false) }
              ) { Text("Deny") }

              TextButton(
                onClick = scopedAction {
                  requestHandlers[keysByPermission[permission]!!]!!().requestPermission(permission)
                  appUiStarter.startAppUi()
                }
              ) { Text("Allow") }
            }
          }
        )
      }
    }
  }
}


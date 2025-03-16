/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import essentials.*
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*
import kotlin.reflect.*

class PermissionRequestScreen(
  val permissionsKeys: List<KClass<out Permission>>
) : CriticalUserFlowScreen<Boolean>

@Provide @Composable fun PermissionRequestUi(
  launchUi: launchUi,
  navigator: Navigator,
  permissionManager: PermissionManager,
  requestHandlers: Map<KClass<out Permission>, suspend (Permission) -> PermissionRequestResult<Permission>>,
  screen: PermissionRequestScreen
): Ui<PermissionRequestScreen> {
  val keysByPermission = remember {
    screen.permissionsKeys.associateBy { permissionManager.permission(it) }
  }

  val permissionStates = keysByPermission
    .mapValues { (permission, key) ->
      key(permission) {
        produceScopedState(nullOf()) {
          permissionManager.permissionState(listOf(key))
            .collect { value = it }
        }.value
      }
    }

  LaunchedScopedEffect(permissionStates) {
    if (permissionStates.all { it.value == true })
      navigator.pop(screen, true)
  }

  val isLoading = permissionStates.any { it.value == null }

  val permissionsToGrant = permissionStates
    .filter { it.value == false }
    .keys
    .toList()

  EsScaffold(topBar = { EsAppBar { Text("Required permissions") } }) {
    if (isLoading) CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
    else EsLazyColumn {
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
                  requestHandlers[keysByPermission[permission]!!]!!(permission)
                  permissionRefreshes.emit(Unit)
                  launchUi()
                }
              ) { Text("Allow") }
            }
          }
        )
      }
    }
  }
}


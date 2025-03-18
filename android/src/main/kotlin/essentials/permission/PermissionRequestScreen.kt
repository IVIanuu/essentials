/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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
  permissions: Permissions,
  requestHandlers: Map<KClass<out Permission>, suspend (Permission) -> PermissionRequestResult<Permission>>,
  screen: PermissionRequestScreen,
  context: ScreenContext<PermissionRequestScreen> = inject
): Ui<PermissionRequestScreen> {
  val keysByPermission = remember {
    screen.permissionsKeys.associateBy { permissions.permission(it) }
  }

  val permissionStates = keysByPermission
    .mapValues { (permission, key) ->
      key(permission) {
        produceScopedState(nullOf()) {
          permissions.permissionState(listOf(key))
            .collect { value = it }
        }.value
      }
    }

  LaunchedScopedEffect(permissionStates) {
    if (permissionStates.all { it.value == true })
      popWithResult(true)
  }

  val isLoading = permissionStates.any { it.value == null }

  val permissionsToGrant = permissionStates
    .filter { it.value == false }
    .keys
    .toList()

  EsScaffold(topBar = { EsAppBar { Text("Required permissions") } }) {
    if (isLoading) CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
    else EsLazyColumn {
      itemsIndexed(permissionsToGrant, { _, permission -> permission }) { index, permission ->
        DecoratedListItem(
          modifier = Modifier.animateItem(),
          first = index == 0,
          last = index == permissionsToGrant.lastIndex,
          headlineContent = { Text(permission.title) },
          supportingContent = permission.desc?.let { { Text(it) } },
          leadingContent = { permission.icon?.invoke() },
          trailingContent = {
            Row(horizontalArrangement = Arrangement.End) {
              TextButton(
                onClick = action { popWithResult(false) }
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


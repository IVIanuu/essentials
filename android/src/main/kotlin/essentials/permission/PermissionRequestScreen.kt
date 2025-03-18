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
) : OverlayScreen<Boolean>

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

  EsModalBottomSheet {
    Subheader { Text("Permissions required") }

    if (isLoading) CircularProgressIndicator(modifier = Modifier
      .fillMaxSize()
      .wrapContentSize())
    else EsLazyColumn {
      itemsIndexed(permissionsToGrant, { _, permission -> permission }) { index, permission ->
        SectionAlert(
          modifier = Modifier.animateItem(),
          sectionType = sectionTypeOf(index, permissionsToGrant.size),
          title = { Text(permission.title) },
          text = { Text(permission.desc, modifier = Modifier.padding(it)) },
          icon = { permission.icon?.invoke() },
          actions = {
            Button(
              colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.onTertiary.copy(ContentAlpha.Medium),
                MaterialTheme.colorScheme.tertiary.copy(ContentAlpha.Medium)
              ),
              onClick = action { popWithResult(false) }
            ) { Text("Deny") }

            Button(
              colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.onTertiary
              ),
              onClick = scopedAction {
                requestHandlers[keysByPermission[permission]!!]!!(permission)
                permissionRefreshes.emit(Unit)
                launchUi()
              }
            ) { Text("Allow") }
          }
        )
      }
    }
  }
}


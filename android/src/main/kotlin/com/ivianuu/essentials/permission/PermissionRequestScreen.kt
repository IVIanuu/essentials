/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

class PermissionRequestScreen(
  val permissionsKeys: List<TypeKey<Permission>>
) : CriticalUserFlowScreen<Boolean> {
  @Provide companion object {
    @Provide fun ui(
      appUiStarter: AppUiStarter,
      navigator: Navigator,
      permissionManager: PermissionManager,
      requestHandlers: Map<TypeKey<Permission>, () -> PermissionRequestHandler<Permission>>,
      screen: PermissionRequestScreen
    ) = Ui<PermissionRequestScreen> {
      LaunchedEffect(true) {
        permissionManager.permissionState(screen.permissionsKeys).first { it }
        navigator.pop(screen, true)
      }

      val keysByPermission = remember {
        screen.permissionsKeys.associateBy { permissionManager.permission(it) }
      }

      val permissionsToGrant = keysByPermission
        .keys
        .filterNot {
          key(keysByPermission[it]) {
            permissionManager.permissionState(listOf(keysByPermission[it]!!))
              .collect(false)
          }
        }

      ScreenScaffold(topBar = { AppBar { Text("Required permissions") } }) {
        VerticalList {
          items(permissionsToGrant) { permission ->
            ListItem(
              modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                .border(
                  1.dp,
                  LocalContentColor.current.copy(alpha = 0.12f),
                  RoundedCornerShape(8.dp)
                ),
              textPadding = PaddingValues(start = 16.dp),
              title = { Text(permission.title) },
              subtitle = permission.desc?.let { { Text(it) } },
              leading = { permission.icon?.invoke() },
              trailing = {
                Row(horizontalArrangement = Arrangement.End) {
                  TextButton(
                    modifier = Modifier.width(56.dp),
                    onClick = action { navigator.pop(screen, false) }
                  ) { Text("Deny") }

                  TextButton(
                    modifier = Modifier.width(56.dp),
                    onClick = scopedAction {
                      requestHandlers[keysByPermission[permission]!!]!!()(permission)
                      appUiStarter()
                    }
                  ) { Text("Allow") }
                }
              }
            )
          }
        }
      }
    }
  }
}

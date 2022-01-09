/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.hidenavbar.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide val navBarHomeItem = HomeItem("Nav bar") { NavBarKey }

object NavBarKey : Key<Unit>

@Provide fun navBarUi(
  navigator: Navigator,
  navBarPref: DataStore<NavBarPrefs>,
  permissionState: Flow<PermissionState<NavBarPermission>>,
  permissionRequester: PermissionRequester,
  scope: NamedCoroutineScope<KeyUiScope>
) = KeyUi<NavBarKey> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Nav bar settings") }) }
  ) {
    Column(
      modifier = Modifier.center(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      val navBarPrefs by navBarPref.data.collectAsState(NavBarPrefs())
      // reshow nav bar when leaving the screen
      DisposableEffect(true) {
        onDispose {
          scope.launch {
            navBarPref.updateData {
              copy(hideNavBar = false)
            }
          }
        }
      }

      val hasPermission by permissionState.collectAsState(false)

      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (hasPermission) {
            if (navBarPrefs.hideNavBar) {
              "Nav bar hidden"
            } else {
              if (forceNavBarVisible.value) {
                "Nav bar forced shown"
              } else {
                "Nav bar shown"
              }
            }
          } else {
            "Unknown nav bar state"
          },
          style = MaterialTheme.typography.h3
        )
      }

      Button(
        onClick = {
          if (hasPermission) {
            scope.launch {
              navBarPref.updateData {
                copy(hideNavBar = !hideNavBar)
              }
            }
          } else {
            scope.launch {
              permissionRequester(listOf(typeKeyOf<NavBarPermission>()))
            }
          }
        }
      ) {
        Text("Toggle nav bar")
      }

      Spacer(Modifier.height(8.dp))

      Button(
        onClick = { forceNavBarVisible = ForceNavBarVisibleState(!forceNavBarVisible.value) }
      ) { Text("Toggle force nav bar") }

      Spacer(Modifier.height(8.dp))

      Button(
        onClick = {
          scope.launch {
            navigator.push(com.ivianuu.essentials.hidenavbar.ui.NavBarKey)
          }
        }
      ) { Text("Settings") }
    }
  }
}

private var forceNavBarVisible by mutableStateOf(ForceNavBarVisibleState(false))

@Provide val sampleForceNavBarVisibleState: @Composable () -> ForceNavBarVisibleState = {
  forceNavBarVisible
}

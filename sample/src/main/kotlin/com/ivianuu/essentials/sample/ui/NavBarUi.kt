/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.hidenavbar.ForceNavBarVisibleState
import com.ivianuu.essentials.hidenavbar.NavBarPermission
import com.ivianuu.essentials.hidenavbar.NavBarPrefs
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow

@Provide val navBarHomeItem = HomeItem("Nav bar") { NavBarKey }

object NavBarKey : Key<Unit>

@Provide fun navBarUi(
  navigator: Navigator,
  navBarPref: DataStore<NavBarPrefs>,
  permissionState: Flow<PermissionState<NavBarPermission>>,
  permissionRequester: PermissionRequester,
  S: NamedCoroutineScope<KeyUiScope>
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
          launch {
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
            launch {
              navBarPref.updateData {
                copy(hideNavBar = !hideNavBar)
              }
            }
          } else {
            launch {
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
          launch {
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

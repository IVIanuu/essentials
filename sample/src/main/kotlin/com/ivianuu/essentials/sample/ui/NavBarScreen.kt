/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.hidenavbar.ForceNavBarVisibleState
import com.ivianuu.essentials.hidenavbar.NavBarPermission
import com.ivianuu.essentials.hidenavbar.NavBarPrefs
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.MutableStateFlow

@Provide val navBarHomeItem = HomeItem("Nav bar") { NavBarScreen() }

class NavBarScreen : Screen<Unit>

@Provide fun navBarUi(
  navigator: Navigator,
  navBarPref: DataStore<NavBarPrefs>,
  permissionManager: PermissionManager
) = Ui<NavBarScreen, Unit> {
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
      LaunchedEffect(true) {
        onCancel {
          navBarPref.updateData {
            copy(hideNavBar = false)
          }
        }
      }

      val hasPermission by permissionManager.permissionState(listOf(typeKeyOf<NavBarPermission>()))
        .collectAsState(
          false
        )

      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (hasPermission) {
            when {
              sampleForceNavBarVisibleState.collectAsState().value.value -> "Nav bar forced shown"
              navBarPrefs.hideNavBar -> "Nav bar hidden"
              else -> "Nav bar shown"
            }
          } else "Unknown nav bar state",
          style = MaterialTheme.typography.displaySmall
        )
      }

      Button(
        onClick = action {
          if (hasPermission) {
            navBarPref.updateData {
              copy(hideNavBar = !hideNavBar)
            }
          } else {
            permissionManager.requestPermissions(listOf(typeKeyOf<NavBarPermission>()))
          }
        }
      ) {
        Text("Toggle nav bar")
      }

      Spacer(Modifier.height(8.dp))

      Button(
        onClick = {
          sampleForceNavBarVisibleState.value =
            ForceNavBarVisibleState(!sampleForceNavBarVisibleState.value.value)
        }
      ) { Text("Toggle force nav bar") }

      Spacer(Modifier.height(8.dp))

      Button(
        onClick = action {
          navigator.push(com.ivianuu.essentials.hidenavbar.ui.NavBarScreen())
        }
      ) { Text("Settings") }
    }
  }
}

@Provide val sampleForceNavBarVisibleState =
  MutableStateFlow(ForceNavBarVisibleState(false))

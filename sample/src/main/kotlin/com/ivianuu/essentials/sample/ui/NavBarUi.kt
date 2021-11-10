/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Provide val navBarHomeItem = HomeItem("Nav bar") { NavBarKey }

object NavBarKey : Key<Unit>

@Provide fun navBarUi(
  forceNavBarVisibleState: MutableStateFlow<ForceNavBarVisibleState>,
  navigator: Navigator,
  navBarPref: DataStore<NavBarPrefs>,
  permissionState: Flow<PermissionState<NavBarPermission>>,
  permissionRequester: PermissionRequester,
  S: ComponentScope<KeyUiComponent>
): KeyUi<NavBarKey> = {
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
              if (forceNavBarVisibleState.collectAsState().value.value) {
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
        onClick = {
          forceNavBarVisibleState.value =
            ForceNavBarVisibleState(!forceNavBarVisibleState.value.value)
        }
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

@Provide val sampleForceNavBarVisibleState = MutableStateFlow(ForceNavBarVisibleState(false))

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

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.injekt.Provide

@Provide val drawerHomeItem = HomeItem("Drawer") { DrawerKey }

object DrawerKey : Key<Unit>

@Provide val drawerUi = KeyUi<DrawerKey> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Drawer") }) },
    drawerContent = {
      Surface(color = Color.Blue) {
        Text(
          text = "Drawer",
          style = MaterialTheme.typography.h4,
          modifier = Modifier.center()
        )
      }
    }
  ) {
    Surface(color = Color.Red) {
      Text(
        text = "Body",
        style = MaterialTheme.typography.h4,
        modifier = Modifier.center()
      )
    }
  }
}

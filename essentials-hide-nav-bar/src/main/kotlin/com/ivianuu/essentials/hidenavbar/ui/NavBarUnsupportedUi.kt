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

package com.ivianuu.essentials.hidenavbar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.injekt.Provide

object NavBarUnsupportedKey : Key<Unit>

@Provide val navBarUnsupportedUi: ModelKeyUi<NavBarUnsupportedKey, NavBarUnsupportedModel> = {
  Scaffold(
    topBar = { TopAppBar(title = { Text(R.string.es_nav_bar_unsupported_title) }) }
  ) {
    Column {
      Text(
        modifier = Modifier.padding(16.dp),
        textResId = R.string.es_nav_bar_unsupported_content,
        style = MaterialTheme.typography.body2
      )

      Row {
        Button(
          modifier = Modifier
            .width(200.dp)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
          onClick = model.openMoreInfos
        ) {
          Text(R.string.es_more_infos)
        }

        Button(
          modifier = Modifier
            .width(200.dp)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
          onClick = model.openRootMethod
        ) {
          Text(R.string.es_root_method)
        }
      }
    }
  }
}

data class NavBarUnsupportedModel(val openMoreInfos: () -> Unit, val openRootMethod: () -> Unit)

@Provide fun navBarUnsupportedModel(
  ctx: KeyUiContext<NavBarUnsupportedKey>
): @Composable () -> NavBarUnsupportedModel = {
  NavBarUnsupportedModel(
    openMoreInfos = action {
      ctx.navigator.push(
        com.ivianuu.essentials.ui.android.navigation.UrlKey(
          "https://www.xda-developers.com/google-confirms-overscan-gone-android-11-crippling-third-party-gesture-apps/"
        )
      )
    },
    openRootMethod = action {
      ctx.navigator.push(
        com.ivianuu.essentials.ui.android.navigation.UrlKey(
          "https://forum.xda-developers.com/t/how-to-remove-nav-bar-in-android-11.4190469/"
        )
      )
    }
  )
}

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

import androidx.compose.material.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

object NavBarUnsupportedKey : DialogKey<Nothing>

@Provide val navBarUnsupportedUi: ModelKeyUi<NavBarUnsupportedKey, NavBarUnsupportedModel> = {
  DialogScaffold {
    Dialog(
      title = {
        Text(stringResource(R.string.es_nav_bar_unsupported_title))
      },
      content = {
        Text(stringResource(R.string.es_nav_bar_unsupported_content))
      },
      buttons = {
        TextButton(onClick = model.openMoreInfos) {
          Text(stringResource(R.string.es_more_infos))
        }

        TextButton(onClick = model.close) {
          Text(stringResource(R.string.es_close))
        }
      }
    )
  }
}

@Optics data class NavBarUnsupportedModel(
  val openMoreInfos: () -> Unit = {},
  val close: () -> Unit = {}
)

@Provide fun navBarUnsupportedModel(
  key: NavBarUnsupportedKey,
  navigator: Navigator,
  scope: InjektCoroutineScope<KeyUiScope>
): @Scoped<KeyUiScope> StateFlow<NavBarUnsupportedModel> = scope.state(
  NavBarUnsupportedModel()
) {
  action(NavBarUnsupportedModel.openMoreInfos()) {
    navigator.push(
      UrlKey(
        "https://www.xda-developers.com/google-confirms-overscan-gone-android-11-crippling-third-party-gesture-apps/"
      )
    )
  }
  action(NavBarUnsupportedModel.close()) { navigator.pop(key) }
}

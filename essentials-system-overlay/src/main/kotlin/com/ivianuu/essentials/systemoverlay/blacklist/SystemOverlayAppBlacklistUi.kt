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

package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.runtime.remember
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.apps.ui.DefaultAppPredicate
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

object SystemOverlayAppBlacklistKey : Key<Unit>

@Provide fun systemOverlayAppBlacklistUi(
  checkableAppsPageFactory: (@Provide CheckableAppsParams) -> CheckableAppsScreen,
  rp: ResourceProvider
): ModelKeyUi<SystemOverlayAppBlacklistKey, SystemOverlayAppBlacklistModel> = {
  remember {
    checkableAppsPageFactory(
      CheckableAppsParams(
        checkedApps = model.appBlacklist,
        onCheckedAppsChanged = model.updateAppBlacklist,
        appPredicate = DefaultAppPredicate,
        appBarTitle = loadResource(R.string.es_system_overlay_blacklist_title)
      )
    )
  }.invoke()
}

@Optics data class SystemOverlayAppBlacklistModel(
  val appBlacklist: Flow<Set<String>> = emptyFlow(),
  val updateAppBlacklist: (Set<String>) -> Unit = {}
)

@Provide fun systemOverlayAppBlacklistModel(
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  scope: NamedCoroutineScope<KeyUiScope>
): StateFlow<SystemOverlayAppBlacklistModel> =
  scope.state(SystemOverlayAppBlacklistModel()) {
    update { copy(appBlacklist = pref.data.map { it.appBlacklist }) }
    action(SystemOverlayAppBlacklistModel.updateAppBlacklist()) { appBlacklist ->
      pref.updateData { copy(appBlacklist = appBlacklist) }
    }
  }

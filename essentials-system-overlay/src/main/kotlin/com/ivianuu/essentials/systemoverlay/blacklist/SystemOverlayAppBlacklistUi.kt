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

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.checkableapps.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

object SystemOverlayAppBlacklistKey : Key<Nothing>

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
  scope: InjektCoroutineScope<KeyUiScope>
): @Scoped<KeyUiScope> StateFlow<SystemOverlayAppBlacklistModel> =
  scope.state(SystemOverlayAppBlacklistModel()) {
    update { copy(appBlacklist = pref.data.map { it.appBlacklist }) }
    action(SystemOverlayAppBlacklistModel.updateAppBlacklist()) { appBlacklist ->
      pref.updateData { copy(appBlacklist = appBlacklist) }
    }
  }

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.runtime.*
import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.checkableapps.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

@Provide val checkAppsHomeItem = HomeItem("Check apps") { CheckAppsKey }

object CheckAppsKey : Key<Unit>

@Provide fun checkAppsUi(
  checkableAppsScreen: (CheckableAppsParams) -> CheckableAppsScreen,
  store: DataStore<List<CheckedAppEntity>>,
  launchableAppPredicate: LaunchableAppPredicate,
  scope: NamedCoroutineScope<KeyUiScope>
) = SimpleKeyUi<CheckAppsKey> {
  remember {
    checkableAppsScreen(
      CheckableAppsParams(
        store.data
          .map { it.mapTo(mutableSetOf()) { it.packageName } },
        { checkedApps ->
          scope.launch {
            store.updateData {
              checkedApps
                .map { CheckedAppEntity(it) }
            }
          }
        },
        launchableAppPredicate,
        "Send check apps"
      )
    )
  }()
}

@Serializable data class CheckedAppEntity(val packageName: String) {
  companion object {
    @Provide val storeModule =
      DataStoreModule<List<CheckedAppEntity>>("checked_apps") { emptyList() }
  }
}

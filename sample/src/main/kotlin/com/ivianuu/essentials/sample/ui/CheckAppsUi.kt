/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.runtime.remember
import com.ivianuu.essentials.android.prefs.DataStoreModule
import com.ivianuu.essentials.apps.ui.LaunchableAppPredicate
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

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

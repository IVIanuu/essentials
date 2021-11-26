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

import androidx.compose.runtime.remember
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.apps.ui.LaunchableAppPredicate
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.db.AbstractEntityDescriptor
import com.ivianuu.essentials.db.AndroidDb
import com.ivianuu.essentials.db.Db
import com.ivianuu.essentials.db.PrimaryKey
import com.ivianuu.essentials.db.Schema
import com.ivianuu.essentials.db.deleteAll
import com.ivianuu.essentials.db.insertAll
import com.ivianuu.essentials.db.selectAll
import com.ivianuu.essentials.state.asComposable
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

@Provide val checkAppsHomeItem = HomeItem("Check apps") { CheckAppsKey }

object CheckAppsKey : Key<Unit>

@Provide fun checkAppsUi(
  checkableAppsScreen: (CheckableAppsParams) -> CheckableAppsScreen,
  db: @CheckApps Db,
  launchableAppPredicate: LaunchableAppPredicate,
  S: NamedCoroutineScope<KeyUiScope>
) = KeyUi<CheckAppsKey> {
  remember {
    checkableAppsScreen(
      CheckableAppsParams(
        db.selectAll<CheckedAppEntity>()
          .map { it.map { it.packageName }.toSet() }
          .asComposable(emptySet()),
        { checkedApps ->
          launch {
            db.transaction {
              db.deleteAll<CheckedAppEntity>()
              db.insertAll(
                checkedApps
                  .map { CheckedAppEntity(it) }
              )
            }
          }
        },
        launchableAppPredicate,
        "Send check apps"
      )
    )
  }.invoke()
}

@Tag private annotation class CheckApps

@Provide fun checkAppsDb(context: AppContext): @Scoped<AppScope> @CheckApps Db = AndroidDb(
  context = context,
  name = "checked_apps.db",
  schema = Schema(
    version = 1,
    entities = listOf(CheckedAppEntity)
  )
)

@Serializable data class CheckedAppEntity(@PrimaryKey val packageName: String) {
  companion object : AbstractEntityDescriptor<CheckedAppEntity>("checked_apps")
}

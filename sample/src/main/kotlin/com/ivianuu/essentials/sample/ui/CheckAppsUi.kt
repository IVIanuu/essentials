/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.runtime.remember
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.apps.ui.LaunchableAppPredicate
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.db.AbstractEntityDescriptor
import com.ivianuu.essentials.db.AndroidDb
import com.ivianuu.essentials.db.Db
import com.ivianuu.essentials.db.PrimaryKey
import com.ivianuu.essentials.db.Schema
import com.ivianuu.essentials.db.deleteAll
import com.ivianuu.essentials.db.insertAll
import com.ivianuu.essentials.db.selectAll
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Provide val checkAppsHomeItem = HomeItem("Check apps") { CheckAppsKey }

object CheckAppsKey : Key<Unit>

@Provide fun checkAppsUi(
  checkableAppsScreen: (CheckableAppsParams) -> CheckableAppsScreen,
  db: @CheckApps Db,
  launchableAppPredicate: LaunchableAppPredicate,
  scope: NamedCoroutineScope<KeyUiScope>
) = SimpleKeyUi<CheckAppsKey> {
  remember {
    checkableAppsScreen(
      CheckableAppsParams(
        db.selectAll<CheckedAppEntity>()
          .map { it.map { it.packageName }.toSet() },
        { checkedApps ->
          scope.launch {
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
  }()
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

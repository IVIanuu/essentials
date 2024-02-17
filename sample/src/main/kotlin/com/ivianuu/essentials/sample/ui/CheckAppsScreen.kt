/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.runtime.remember
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.apps.CheckableAppsScreen
import com.ivianuu.essentials.apps.CheckableAppsUi
import com.ivianuu.essentials.apps.LaunchableAppPredicate
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.db.Db
import com.ivianuu.essentials.db.DbFactory
import com.ivianuu.essentials.db.Entity
import com.ivianuu.essentials.db.EntityDescriptor
import com.ivianuu.essentials.db.PrimaryKey
import com.ivianuu.essentials.db.Schema
import com.ivianuu.essentials.db.deleteAll
import com.ivianuu.essentials.db.insertAll
import com.ivianuu.essentials.db.selectAll
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.ScreenScope
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Provide val checkAppsHomeItem = HomeItem("Check apps") { CheckAppsScreen() }

class CheckAppsScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      checkableAppsUi: (CheckableAppsScreen) -> CheckableAppsUi,
      db: @CheckApps Db,
      launchableAppPredicate: LaunchableAppPredicate,
      scope: ScopedCoroutineScope<ScreenScope>
    ) = Ui<CheckAppsScreen, Unit> {
      remember {
        checkableAppsUi(
          CheckableAppsScreen(
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
      }.Content()
    }
  }
}

@Tag private annotation class CheckApps

@Provide fun checkAppsDb(dbFactory: DbFactory): @Scoped<AppScope> @CheckApps Db = dbFactory(
  name = "checked_apps.db",
  schema = Schema(
    version = 1,
    entities = listOf(EntityDescriptor<CheckedAppEntity>("checked_apps"))
  )
)

@Serializable @Entity data class CheckedAppEntity(@PrimaryKey val packageName: String)

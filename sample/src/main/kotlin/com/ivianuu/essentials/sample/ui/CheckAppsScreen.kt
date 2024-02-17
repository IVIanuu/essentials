/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.db.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

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

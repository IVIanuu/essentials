/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.checkableapps.*
import com.ivianuu.essentials.db.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

@Provide val checkAppsHomeItem = HomeItem("Check apps") { CheckAppsKey }

object CheckAppsKey : Key<Unit>

@Provide fun checkAppsUi(
  checkableAppsScreen: (CheckableAppsParams) -> CheckableAppsScreen,
  db: @CheckApps Db,
  launchableAppPredicate: LaunchableAppPredicate,
  scope: NamedCoroutineScope<KeyUiScope>
) = KeyUi<CheckAppsKey> {
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

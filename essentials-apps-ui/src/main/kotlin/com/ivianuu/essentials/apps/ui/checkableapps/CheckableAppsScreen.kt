/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps.ui.checkableapps

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import coil.compose.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.popup.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.injekt.*

fun interface CheckableAppsScreen : @Composable () -> Unit

data class CheckableAppsParams(
  val checkedApps: @Composable () -> Set<String>,
  val onCheckedAppsChanged: (Set<String>) -> Unit,
  val appPredicate: AppPredicate,
  val appBarTitle: String
)

@Provide fun checkableAppsScreen(models: @Composable () -> CheckableAppsModel) = CheckableAppsScreen {
  val model = models()
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(model.appBarTitle) },
        actions = {
          PopupMenuButton(
            items = listOf(
              PopupMenu.Item(onSelected = model.selectAll) {
                Text(R.string.es_select_all)
              },
              PopupMenu.Item(onSelected = model.deselectAll) {
                Text(R.string.es_deselect_all)
              }
            )
          )
        }
      )
    }
  ) {
    ResourceVerticalListFor(model.checkableApps) { app ->
      ListItem(
        modifier = Modifier.clickable { model.updateAppCheckedState(app, !app.isChecked) },
        title = { Text(app.info.appName) },
        leading = {
          Image(
            painter = rememberImagePainter(AppIcon(packageName = app.info.packageName)),
            modifier = Modifier.size(40.dp)
          )
        },
        trailing = {
          Switch(
            checked = app.isChecked,
            onCheckedChange = null
          )
        }
      )
    }
  }
}

data class CheckableAppsModel(
  val allApps: Resource<List<AppInfo>>,
  val checkedApps: Set<String>,
  val appPredicate: AppPredicate,
  val appBarTitle: String,
  val updateAppCheckedState: (CheckableApp, Boolean) -> Unit,
  val selectAll: () -> Unit,
  val deselectAll: () -> Unit
) {
  val checkableApps = allApps
    .map { it.filter(appPredicate) }
    .map { apps ->
      apps.map { app ->
        CheckableApp(
          info = app,
          isChecked = app.packageName in checkedApps
        )
      }
    }
}

data class CheckableApp(val info: AppInfo, val isChecked: Boolean)

@Provide @Composable fun checkableAppsModel(
  appRepository: AppRepository,
  params: CheckableAppsParams
): CheckableAppsModel {
  val checkedApps = params.checkedApps()
  val allApps = resourceFromFlow { appRepository.installedApps }

  fun pushNewCheckedApps(transform: Set<String>.() -> Set<String>) {
    val newCheckedApps = checkedApps.transform()
    params.onCheckedAppsChanged(newCheckedApps)
  }

  return CheckableAppsModel(
    allApps = allApps,
    appPredicate = params.appPredicate,
    appBarTitle = params.appBarTitle,
    checkedApps = checkedApps,
    updateAppCheckedState = action { app, isChecked ->
      pushNewCheckedApps {
        if (isChecked) this + app.info.packageName
        else this - app.info.packageName
      }
    },
    selectAll = action {
      pushNewCheckedApps {
        this + allApps.get().map { it.packageName }
      }
    },
    deselectAll = action {
      pushNewCheckedApps { emptySet() }
    }
  )
}

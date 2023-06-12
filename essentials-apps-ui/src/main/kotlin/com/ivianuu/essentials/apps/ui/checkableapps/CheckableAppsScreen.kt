/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps.ui.checkableapps

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.ui.AppPredicate
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.bind
import com.ivianuu.essentials.compose.bindResource
import com.ivianuu.essentials.compose.state
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.get
import com.ivianuu.essentials.resource.map
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.ui.popup.PopupMenuItem
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

fun interface CheckableAppsScreen {
  @Composable operator fun invoke()
}

data class CheckableAppsParams(
  val checkedApps: Flow<Set<String>>,
  val onCheckedAppsChanged: (Set<String>) -> Unit,
  val appPredicate: AppPredicate,
  val appBarTitle: String
)

@Provide fun checkableAppsScreen(models: StateFlow<CheckableAppsModel>) = CheckableAppsScreen {
  val model by models.collectAsState()
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(model.appBarTitle) },
        actions = {
          PopupMenuButton {
            PopupMenuItem(onSelected = model.selectAll) { Text(R.string.es_select_all) }
            PopupMenuItem(onSelected = model.deselectAll) { Text(R.string.es_deselect_all) }
          }
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
            painter = rememberAsyncImagePainter(AppIcon(packageName = app.info.packageName)),
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
    .map { it.filter { appPredicate(it) } }
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

@Provide fun checkableAppsModel(
  params: CheckableAppsParams,
  repository: AppRepository,
  scope: NamedCoroutineScope<KeyUiScope>
): @Scoped<KeyUiScope> StateFlow<CheckableAppsModel> = scope.state {
  val checkedApps = params.checkedApps.bind(emptySet())
  val allApps = repository.installedApps.bindResource()

  fun pushNewCheckedApps(transform: Set<String>.() -> Set<String>) {
    val newCheckedApps = checkedApps.transform()
    params.onCheckedAppsChanged(newCheckedApps)
  }

  CheckableAppsModel(
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

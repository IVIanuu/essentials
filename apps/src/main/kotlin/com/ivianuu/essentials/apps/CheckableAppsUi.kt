/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.compositionStateFlow
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.collectAsResourceState
import com.ivianuu.essentials.resource.get
import com.ivianuu.essentials.resource.map
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.navigation.ScreenScope
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.ui.popup.PopupMenuItem
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

fun interface CheckableAppsUi {
  @Composable operator fun invoke()
}

data class CheckableAppsParams(
  val checkedApps: Flow<Set<String>>,
  val onCheckedAppsChanged: (Set<String>) -> Unit,
  val appPredicate: AppPredicate,
  val appBarTitle: String
)

@Provide fun checkableAppsScreen(presenter: StateFlow<CheckableAppsState>) = CheckableAppsUi {
  val state by presenter.collectAsState()
  Scaffold(
    topBar = {
      AppBar(
        title = { Text(state.appBarTitle) },
        actions = {
          PopupMenuButton {
            PopupMenuItem(onSelected = state.selectAll) { Text(stringResource(R.string.es_select_all)) }
            PopupMenuItem(onSelected = state.deselectAll) { Text(stringResource(R.string.es_deselect_all)) }
          }
        }
      )
    }
  ) {
    ResourceVerticalListFor(state.checkableApps) { app ->
      ListItem(
        modifier = Modifier.clickable { state.updateAppCheckedState(app, !app.isChecked) },
        title = { Text(app.info.appName) },
        leading = {
          Image(
            painter = rememberAsyncImagePainter(AppIcon(packageName = app.info.packageName)),
            modifier = Modifier.size(40.dp),
            contentDescription = null
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

data class CheckableAppsState(
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

@Provide fun checkableAppsPresenter(
  params: CheckableAppsParams,
  repository: AppRepository,
  scope: ScopedCoroutineScope<ScreenScope>
): @Scoped<ScreenScope> StateFlow<CheckableAppsState> = scope.compositionStateFlow {
  val checkedApps by params.checkedApps.collectAsState(emptySet())
  val allApps by repository.installedApps.collectAsResourceState()

  fun updateCheckedApps(transform: Set<String>.() -> Set<String>) {
    val newCheckedApps = checkedApps.transform()
    params.onCheckedAppsChanged(newCheckedApps)
  }

  CheckableAppsState(
    allApps = allApps,
    appPredicate = params.appPredicate,
    appBarTitle = params.appBarTitle,
    checkedApps = checkedApps,
    updateAppCheckedState = action { app, isChecked ->
      updateCheckedApps {
        if (isChecked) this + app.info.packageName
        else this - app.info.packageName
      }
    },
    selectAll = action {
      updateCheckedApps {
        this + allApps.get().map { it.packageName }
      }
    },
    deselectAll = action {
      updateCheckedApps { emptySet() }
    }
  )
}

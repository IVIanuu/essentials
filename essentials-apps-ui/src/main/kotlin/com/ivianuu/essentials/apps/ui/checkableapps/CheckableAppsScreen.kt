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

package com.ivianuu.essentials.apps.ui.checkableapps

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.ui.AppIcon
import com.ivianuu.essentials.apps.ui.AppPredicate
import com.ivianuu.essentials.apps.ui.DefaultAppPredicate
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowAsResource
import com.ivianuu.essentials.resource.get
import com.ivianuu.essentials.resource.map
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

typealias CheckableAppsScreen = @Composable () -> Unit

data class CheckableAppsParams(
  val checkedApps: Flow<Set<String>>,
  val onCheckedAppsChanged: (Set<String>) -> Unit,
  val appPredicate: AppPredicate,
  val appBarTitle: String
)

@Provide fun checkableAppsScreen(modelFlow: StateFlow<CheckableAppsModel>): CheckableAppsScreen = {
  val model by modelFlow.collectAsState()
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
        },
        onClick = { model.updateAppCheckedState(app, !app.isChecked) }
      )
    }
  }
}

@Optics data class CheckableAppsModel(
  val allApps: Resource<List<AppInfo>> = Idle,
  val checkedApps: Set<String> = emptySet(),
  val appPredicate: AppPredicate = DefaultAppPredicate,
  val appBarTitle: String,
  val updateAppCheckedState: (CheckableApp, Boolean) -> Unit = { _, _ -> },
  val selectAll: () -> Unit = {},
  val deselectAll: () -> Unit = {}
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

@Provide fun checkableAppsModel(
  appRepository: AppRepository,
  params: CheckableAppsParams,
  scope: ComponentScope<KeyUiComponent>
): StateFlow<CheckableAppsModel> = scope.state(
  CheckableAppsModel(
    appPredicate = params.appPredicate,
    appBarTitle = params.appBarTitle
  )
) {
  params.checkedApps.update { copy(checkedApps = it) }

  appRepository.installedApps
    .flowAsResource()
    .update { copy(allApps = it) }

  suspend fun pushNewCheckedApps(transform: Set<String>.(CheckableAppsModel) -> Set<String>) {
    val currentState = state.first()
    val newCheckedApps = currentState.checkableApps.get()
      .filter { it.isChecked }
      .mapTo(mutableSetOf()) { it.info.packageName }
      .transform(currentState)
    params.onCheckedAppsChanged(newCheckedApps)
  }

  action(CheckableAppsModel.updateAppCheckedState()) { app, isChecked ->
    pushNewCheckedApps {
      if (isChecked) this + app.info.packageName
      else this - app.info.packageName
    }
  }

  action(CheckableAppsModel.selectAll()) {
    pushNewCheckedApps { currentState ->
      currentState.allApps.get().mapTo(mutableSetOf()) { it.packageName }
    }
  }

  action(CheckableAppsModel.deselectAll()) {
    pushNewCheckedApps { emptySet() }
  }
}

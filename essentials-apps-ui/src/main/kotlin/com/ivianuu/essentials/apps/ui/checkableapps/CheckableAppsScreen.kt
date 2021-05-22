/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.google.accompanist.coil.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.apps.coil.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.popup.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

typealias CheckableAppsScreen = @Composable () -> Unit

data class CheckableAppsParams(
  val checkedApps: Flow<Set<String>>,
  val onCheckedAppsChanged: (Set<String>) -> Unit,
  val appPredicate: AppPredicate,
  val appBarTitle: String
)

@Provide
fun checkableAppsScreen(modelFlow: StateFlow<CheckableAppsModel>): CheckableAppsScreen = {
  val state by modelFlow.collectAsState()
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(state.appBarTitle) },
        actions = {
          PopupMenuButton(
            items = listOf(
              PopupMenu.Item(onSelected = state.selectAll) {
                Text(stringResource(R.string.es_select_all))
              },
              PopupMenu.Item(onSelected = state.deselectAll) {
                Text(stringResource(R.string.es_deselect_all))
              }
            )
          )
        }
      )
    }
  ) {
    ResourceLazyColumnFor(state.checkableApps) { app ->
      ListItem(
        title = { Text(app.info.appName) },
        leading = {
          Image(
            painter = rememberCoilPainter(AppIcon(packageName = app.info.packageName)),
            modifier = Modifier.size(40.dp),
            contentDescription = null
          )
        },
        trailing = {
          Checkbox(
            checked = app.isChecked,
            onCheckedChange = null
          )
        },
        onClick = { state.updateAppCheckedState(app, !app.isChecked) }
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

data class CheckableApp(
  val info: AppInfo,
  val isChecked: Boolean
)

@Provide fun checkableAppsModel(
  params: CheckableAppsParams,
  getInstalledApps: GetInstalledAppsUseCase,
  scope: InjectCoroutineScope<KeyUiScope>
): @Scoped<KeyUiScope> StateFlow<CheckableAppsModel> = scope.state(
  CheckableAppsModel(
    appPredicate = params.appPredicate,
    appBarTitle = params.appBarTitle
  )
) {
  params.checkedApps.update { copy(checkedApps = it) }
  resourceFlow { emit(getInstalledApps()) }
    .update { copy(allApps = it) }
  suspend fun pushNewCheckedApps(transform: Set<String>.(CheckableAppsModel) -> Set<String>) {
    val currentState = state.first()
    val newCheckedApps = currentState.checkableApps.get()
      ?.filter { it.isChecked }
      ?.mapTo(mutableSetOf()) { it.info.packageName }
      ?.transform(currentState)
      ?: return
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
      currentState.allApps.get()!!.mapTo(mutableSetOf()) { it.packageName }
    }
  }
  action(CheckableAppsModel.deselectAll()) {
    pushNewCheckedApps { emptySet() }
  }
}

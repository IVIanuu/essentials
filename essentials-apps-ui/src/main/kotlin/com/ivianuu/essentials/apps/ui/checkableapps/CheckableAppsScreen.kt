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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.ui.AppIcon
import com.ivianuu.essentials.apps.ui.AppPredicate
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.get
import com.ivianuu.essentials.resource.map
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Switch
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.essentials.ui.state.action
import com.ivianuu.essentials.ui.state.resourceFromFlow
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag

@Tag annotation class CheckableAppsScreenTag
typealias CheckableAppsScreen = @CheckableAppsScreenTag @Composable () -> Unit

data class CheckableAppsParams(
  val checkedApps: @Composable () -> Set<String>,
  val onCheckedAppsChanged: (Set<String>) -> Unit,
  val appPredicate: AppPredicate,
  val appBarTitle: String
)

@Provide fun checkableAppsScreen(models: @Composable () -> CheckableAppsModel): CheckableAppsScreen = {
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

@Provide fun checkableAppsModel(
  appRepository: AppRepository,
  params: CheckableAppsParams
): @Composable () -> CheckableAppsModel = {
  val checkedApps = params.checkedApps()
  val allApps = resourceFromFlow { appRepository.installedApps }

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

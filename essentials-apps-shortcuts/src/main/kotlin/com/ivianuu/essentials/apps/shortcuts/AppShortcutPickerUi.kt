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

package com.ivianuu.essentials.apps.shortcuts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.essentials.apps.InstalledApps
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowAsResource
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.util.PackageName
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

object AppShortcutPickerKey : Key<AppShortcut>

@Provide val shortcutPickerUi: ModelKeyUi<AppShortcutPickerKey, AppShortcutPickerModel> = {
  Scaffold(topBar = { TopAppBar(title = { Text(R.string.es_title_app_shortcut_picker) }) }) {
    ResourceLazyColumnFor(model.appShortcuts) { appShortcut ->
      ListItem(
        leading = {
          Image(
            modifier = Modifier.size(40.dp),
            bitmap = remember { appShortcut.icon.toBitmap().toImageBitmap() }
          )
        },
        title = { Text(appShortcut.shortLabel) },
        onClick = { model.pickAppShortcut(appShortcut) }
      )
    }
  }
}

@Optics data class AppShortcutPickerModel(
  val appShortcuts: Resource<List<AppShortcut>> = Idle,
  val pickAppShortcut: (AppShortcut) -> Unit = {}
)

@Provide fun appShortcutPickerModel(
  installedApps: Flow<InstalledApps>,
  appShortcuts: (@Provide PackageName) -> Flow<AppShortcuts>,
  key: AppShortcutPickerKey,
  navigator: Navigator,
  scope: NamedCoroutineScope<KeyUiScope>
): StateFlow<AppShortcutPickerModel> = scope.state(AppShortcutPickerModel()) {
  installedApps
    .flatMapLatest { apps ->
      combine(
        apps
          .map { app ->
            appShortcuts(app.packageName)
              .catch { emit(emptyList()) }
          }
      ) { it.toList().flatten() }
    }
    .flowAsResource()
    .update { copy(appShortcuts = it) }

  action(AppShortcutPickerModel.pickAppShortcut()) { appShortcut ->
    navigator.pop(key, appShortcut)
  }
}

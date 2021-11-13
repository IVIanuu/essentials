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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.ui.android.toImageBitmap
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.essentials.ui.state.action
import com.ivianuu.essentials.ui.state.resourceFromFlow
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

object AppShortcutPickerKey : Key<AppShortcut>

@Provide val appShortcutPickerUi: ModelKeyUi<AppShortcutPickerKey, AppShortcutPickerModel> = {
  Scaffold(topBar = { TopAppBar(title = { Text(R.string.es_title_app_shortcut_picker) }) }) {
    ResourceVerticalListFor(model.appShortcuts) { appShortcut ->
      ListItem(
        modifier = Modifier.clickable { model.pickAppShortcut(appShortcut) },
        leading = {
          Image(
            modifier = Modifier.size(40.dp),
            bitmap = remember { appShortcut.icon.toBitmap().toImageBitmap() }
          )
        },
        title = { Text(appShortcut.shortLabel) }
      )
    }
  }
}

data class AppShortcutPickerModel(
  val appShortcuts: Resource<List<AppShortcut>>,
  val pickAppShortcut: (AppShortcut) -> Unit
)

@Provide @Composable fun appShortcutPickerModel(
  appRepository: AppRepository,
  appShortcutRepository: AppShortcutRepository,
  ctx: KeyUiContext<AppShortcutPickerKey>
) = AppShortcutPickerModel(
  appShortcuts = resourceFromFlow {
    appRepository.installedApps
      .flatMapLatest { apps ->
        combine(
          apps
            .map { app ->
              appShortcutRepository.appShortcuts(app.packageName)
                .catch { emit(emptyList()) }
            }
        ) { it.toList().flatten() }
      }
  },
  pickAppShortcut = action { appShortcut ->
    ctx.navigator.pop(ctx.key, appShortcut)
  }
)

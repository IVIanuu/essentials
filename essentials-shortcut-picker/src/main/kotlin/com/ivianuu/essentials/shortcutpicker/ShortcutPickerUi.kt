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

package com.ivianuu.essentials.shortcutpicker

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.onFailure
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.produceResource
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.toIntentKey
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow

object ShortcutPickerKey : Key<Shortcut>

@Provide val shortcutPickerUi: ModelKeyUi<ShortcutPickerKey, ShortcutPickerModel> = {
  Scaffold(topBar = { TopAppBar(title = { Text(R.string.es_title_shortcut_picker) }) }) {
    ResourceLazyColumnFor(model.shortcuts) { shortcut ->
      ListItem(
        leading = {
          Image(
            modifier = Modifier.size(40.dp),
            painter = BitmapPainter(shortcut.icon)
          )
        },
        title = { Text(shortcut.name) },
        onClick = { model.pickShortcut(shortcut) }
      )
    }
  }
}

@Optics data class ShortcutPickerModel(
  val shortcuts: Resource<List<Shortcut>> = Idle,
  val pickShortcut: (Shortcut) -> Unit = {}
)

@Provide fun shortcutPickerModel(
  getAllShortcuts: GetAllShortcutsUseCase,
  extractShortcut: ExtractShortcutUseCase,
  key: ShortcutPickerKey,
  navigator: Navigator,
  scope: NamedCoroutineScope<KeyUiScope>,
  rp: ResourceProvider,
  toaster: Toaster
): @Scoped<KeyUiScope> StateFlow<ShortcutPickerModel> = scope.state(ShortcutPickerModel()) {
  produceResource({ copy(shortcuts = it) }) { getAllShortcuts() }

  action(ShortcutPickerModel.pickShortcut()) { shortcut ->
    catch {
      val shortcutRequestResult = navigator.push(shortcut.intent.toIntentKey())
        ?.getOrNull()
        ?.data ?: return@catch
      val finalShortcut = extractShortcut(shortcutRequestResult)
      navigator.pop(key, finalShortcut)
    }.onFailure {
      it.printStackTrace()
      showToast(R.string.es_failed_to_pick_shortcut)
    }
  }
}

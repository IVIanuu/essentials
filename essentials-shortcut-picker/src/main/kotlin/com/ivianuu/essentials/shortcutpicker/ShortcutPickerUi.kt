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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi2
import com.ivianuu.essentials.ui.navigation.toIntentKey
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.essentials.ui.state.action
import com.ivianuu.essentials.ui.state.resourceFromFlow
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide

object ShortcutPickerKey : Key<Shortcut>

@Provide val shortcutPickerUi: ModelKeyUi2<ShortcutPickerKey, ShortcutPickerModel> = {
  Scaffold(topBar = { TopAppBar(title = { Text(R.string.es_title_shortcut_picker) }) }) {
    ResourceVerticalListFor(model.shortcuts) { shortcut ->
      ListItem(
        modifier = Modifier.clickable { model.pickShortcut(shortcut) },
        leading = {
          Image(
            modifier = Modifier.size(40.dp),
            painter = remember {
              BitmapPainter(shortcut.icon.toBitmap().toImageBitmap())
            }
          )
        },
        title = { Text(shortcut.name) }
      )
    }
  }
}

data class ShortcutPickerModel(
  val shortcuts: Resource<List<Shortcut>>,
  val pickShortcut: (Shortcut) -> Unit
)

@Provide @Composable fun shortcutPickerModel(
  shortcutRepository: ShortcutRepository,
  T: ToastContext,
  ctx: KeyUiContext<ShortcutPickerKey>
) = ShortcutPickerModel(
  shortcuts = resourceFromFlow { shortcutRepository.shortcuts },
  pickShortcut = action { shortcut ->
    catch {
      val shortcutRequestResult = ctx.navigator.push(shortcut.intent.toIntentKey())
        ?.getOrNull()
        ?.data ?: return@catch
      val finalShortcut = shortcutRepository.extractShortcut(shortcutRequestResult)
      ctx.navigator.pop(ctx.key, finalShortcut)
    }.onFailure {
      it.printStackTrace()
      showToast(R.string.es_failed_to_pick_shortcut)
    }
  }
)


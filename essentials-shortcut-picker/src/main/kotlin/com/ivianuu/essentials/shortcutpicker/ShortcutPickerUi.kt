/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.shortcutpicker

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.bindResource
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.DefaultIntentKey
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.invoke
import com.ivianuu.injekt.Provide

object ShortcutPickerKey : Key<Shortcut>

@Provide val shortcutPickerUi = ModelKeyUi<ShortcutPickerKey, ShortcutPickerModel> {
  Scaffold(topBar = { TopAppBar(title = { Text(R.string.es_title_shortcut_picker) }) }) {
    ResourceVerticalListFor(shortcuts) { shortcut ->
      ListItem(
        modifier = Modifier.clickable { pickShortcut(shortcut) },
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

@Provide fun shortcutPickerModel(
  ctx: KeyUiContext<ShortcutPickerKey>,
  resourceProvider: ResourceProvider,
  repository: ShortcutRepository,
  toaster: Toaster
) = Model {
  ShortcutPickerModel(
    shortcuts = repository.shortcuts.bindResource(),
    pickShortcut = action { shortcut ->
      catch {
        val shortcutRequestResult = ctx.navigator.push(DefaultIntentKey(shortcut.intent))
          ?.getOrNull()
          ?.data ?: return@catch
        val finalShortcut = repository.extractShortcut(shortcutRequestResult)
        ctx.navigator.pop(ctx.key, finalShortcut)
      }.onFailure {
        it.printStackTrace()
        toaster(R.string.es_failed_to_pick_shortcut)
      }
    }
  )
}

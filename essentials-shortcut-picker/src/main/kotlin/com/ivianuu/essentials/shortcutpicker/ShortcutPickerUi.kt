/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.shortcutpicker

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.painter.*
import androidx.compose.ui.unit.*
import androidx.core.graphics.drawable.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.image.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

object ShortcutPickerKey : Key<Shortcut>

@Provide val shortcutPickerUi = ModelKeyUi<ShortcutPickerKey, ShortcutPickerModel> {
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

@Provide fun shortcutPickerModel(
  shortcutRepository: ShortcutRepository,
  T: ToastContext,
  ctx: KeyUiContext<ShortcutPickerKey>
) = ShortcutPickerModel(
  shortcuts = shortcutRepository.shortcuts.bindResource(),
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

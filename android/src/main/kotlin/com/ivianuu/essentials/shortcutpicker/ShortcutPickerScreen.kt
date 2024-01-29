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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.collectAsResourceState
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.result.getOrNull
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.navigation.DefaultIntentScreen
import com.ivianuu.essentials.ui.navigation.Presenter
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide

class ShortcutPickerScreen : Screen<Shortcut>

@Provide val shortcutPickerUi = Ui<ShortcutPickerScreen, ShortcutPickerState> { state ->
  Scaffold(topBar = { AppBar { Text(stringResource(R.string.es_title_shortcut_picker)) } }) {
    ResourceVerticalListFor(state.shortcuts) { shortcut ->
      ListItem(
        modifier = Modifier.clickable { state.pickShortcut(shortcut) },
        leading = {
          Image(
            modifier = Modifier.size(40.dp),
            painter = remember { BitmapPainter(shortcut.icon.toBitmap().toImageBitmap()) },
            contentDescription = null
          )
        },
        title = { Text(shortcut.name) }
      )
    }
  }
}

data class ShortcutPickerState(
  val shortcuts: Resource<List<Shortcut>>,
  val pickShortcut: (Shortcut) -> Unit
)

@Provide fun shortcutPickerPresenter(
  navigator: Navigator,
  repository: ShortcutRepository,
  screen: ShortcutPickerScreen,
  toaster: Toaster
) = Presenter {
  ShortcutPickerState(
    shortcuts = repository.shortcuts.collectAsResourceState().value,
    pickShortcut = action { shortcut ->
      catch {
        val shortcutRequestResult = navigator.push(DefaultIntentScreen(shortcut.intent))
          ?.getOrNull()
          ?.data ?: return@catch
        val finalShortcut = repository.extractShortcut(shortcutRequestResult)
        navigator.pop(screen, finalShortcut)
      }.onFailure {
        it.printStackTrace()
        toaster(R.string.es_failed_to_pick_shortcut)
      }
    }
  )
}

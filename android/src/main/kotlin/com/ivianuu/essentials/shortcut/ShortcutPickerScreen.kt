/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.shortcut

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.core.graphics.drawable.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

class ShortcutPickerScreen : Screen<Shortcut> {
  @Provide companion object {
    @Provide fun shortcutPickerUi(
      navigator: Navigator,
      repository: ShortcutRepository,
      screen: ShortcutPickerScreen,
      toaster: Toaster
    ) = Ui<ShortcutPickerScreen> {
      ScreenScaffold(topBar = { AppBar { Text("Pick an shortcut") } }) {
        ResourceBox(repository.shortcuts.scopedResourceState()) { shortcuts ->
          VerticalList {
            items(shortcuts) { shortcut ->
              ListItem(
                onClick = scopedAction {
                  catch {
                    val shortcutRequestResult = navigator.push(shortcut.intent.asScreen())
                      ?.getOrNull()
                      ?.data ?: return@catch
                    val finalShortcut = repository.extractShortcut(shortcutRequestResult)
                    navigator.pop(screen, finalShortcut)
                  }.onLeft {
                    it.printStackTrace()
                    toaster.toast("Failed to pick a shortcut!")
                  }
                },
                leading = {
                  Image(
                    modifier = Modifier.size(40.dp),
                    bitmap = shortcut.icon.toBitmap().asImageBitmap(),
                    contentDescription = null
                  )
                },
                title = { Text(shortcut.name) }
              )
            }
          }
        }
      }
    }
  }
}

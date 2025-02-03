/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.shortcut

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.core.graphics.drawable.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowAsResource
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
      EsScaffold(topBar = { EsAppBar { Text("Pick an shortcut") } }) {
        ResourceBox(
          produceScopedState(Resource.Idle()) {
            repository.shortcuts
              .flowAsResource()
              .collect { value = it }
          }.value
        ) { shortcuts ->
          EsLazyColumn {
            items(shortcuts) { shortcut ->
              EsListItem(
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
                leadingContent = {
                  Image(
                    modifier = Modifier.size(40.dp),
                    bitmap = shortcut.icon.toBitmap().asImageBitmap(),
                    contentDescription = null
                  )
                },
                headlineContent = { Text(shortcut.name) }
              )
            }
          }
        }
      }
    }
  }
}

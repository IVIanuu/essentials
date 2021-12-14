/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps.shortcuts

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.core.graphics.drawable.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.image.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

object AppShortcutPickerKey : Key<AppShortcut>

@Provide val appShortcutPickerUi = ModelKeyUi<AppShortcutPickerKey, AppShortcutPickerModel> {
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

@Provide fun appShortcutPickerModel(
  appRepository: AppRepository,
  appShortcutRepository: AppShortcutRepository,
  ctx: KeyUiContext<AppShortcutPickerKey>
) = AppShortcutPickerModel(
  appShortcuts = appRepository.installedApps
    .flatMapLatest { apps ->
      combine(
        apps
          .map { app ->
            appShortcutRepository.appShortcuts(app.packageName)
              .catch { emit(emptyList()) }
          }
      ) { it.toList().flatten() }
    }
    .bindResource(),
  pickAppShortcut = action { appShortcut ->
    ctx.navigator.pop(ctx.key, appShortcut)
  }
)

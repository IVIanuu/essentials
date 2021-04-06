/*
 * Copyright 2020 Manuel Wrage
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.resourceFlow
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerAction.PickShortcut
import com.ivianuu.essentials.store.Sink
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.onAction
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.*
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.util.ActivityResultLauncher
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given

class ShortcutPickerKey : Key<Shortcut>

@Given
val shortcutPickerUi: StoreKeyUi<ShortcutPickerKey, ShortcutPickerState, ShortcutPickerAction> = {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.es_title_shortcut_picker)) }
            )
        }
    ) {
        ResourceLazyColumnFor(state.shortcuts) { shortcut ->
            ListItem(
                leading = {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = BitmapPainter(shortcut.icon),
                        contentDescription = null
                    )
                },
                title = { Text(shortcut.name) },
                onClick = { send(PickShortcut(shortcut)) }
            )
        }
    }
}

data class ShortcutPickerState(val shortcuts: Resource<List<Shortcut>> = Idle)

sealed class ShortcutPickerAction {
    data class PickShortcut(val shortcut: Shortcut) : ShortcutPickerAction()
}

@Given
fun shortcutPickerStore(
    @Given activityResultLauncher: ActivityResultLauncher,
    @Given key: ShortcutPickerKey,
    @Given navigator: Sink<NavigationAction>,
    @Given shortcutRepository: ShortcutRepository,
    @Given toaster: Toaster
): StoreBuilder<KeyUiGivenScope, ShortcutPickerState, ShortcutPickerAction> = {
    resourceFlow { emit(shortcutRepository.getAllShortcuts()) }
        .update { copy(shortcuts = it) }
    onAction<PickShortcut> { action ->
        runCatching {
            val shortcutRequestResult = activityResultLauncher.startActivityForResult(action.shortcut.intent)
                .data ?: return@runCatching
            val finalShortcut = shortcutRepository.extractShortcut(shortcutRequestResult)
            navigator.pop(key, finalShortcut)
        }.onFailure {
            it.printStackTrace()
            toaster.showToast(R.string.es_failed_to_pick_shortcut)
        }
    }
}

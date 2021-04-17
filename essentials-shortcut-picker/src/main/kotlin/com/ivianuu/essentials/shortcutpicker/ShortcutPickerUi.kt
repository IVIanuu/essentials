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

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.painter.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

object ShortcutPickerKey : Key<Shortcut>

@Given
val shortcutPickerUi: ModelKeyUi<ShortcutPickerKey, ShortcutPickerModel> = {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.es_title_shortcut_picker)) }
            )
        }
    ) {
        ResourceLazyColumnFor(model.shortcuts) { shortcut ->
            ListItem(
                leading = {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = BitmapPainter(shortcut.icon),
                        contentDescription = null
                    )
                },
                title = { Text(shortcut.name) },
                onClick = { model.pickShortcut(shortcut) }
            )
        }
    }
}

@Optics
data class ShortcutPickerModel(
    val shortcuts: Resource<List<Shortcut>> = Idle,
    val pickShortcut: (Shortcut) -> Unit = {}
)

@Given
fun shortcutPickerModel(
    @Given getAllShortcuts: GetAllShortcutsUseCase,
    @Given extractShortcut: ExtractShortcutUseCase,
    @Given key: ShortcutPickerKey,
    @Given navigator: Navigator,
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given stringResource: StringResourceProvider,
    @Given toaster: Toaster
): @Scoped<KeyUiGivenScope> StateFlow<ShortcutPickerModel> = scope.state(ShortcutPickerModel()) {
    resourceFlow { emit(getAllShortcuts()) }
        .update { copy(shortcuts = it) }
    action(ShortcutPickerModel.pickShortcut()) { shortcut ->
        runCatching {
            val shortcutRequestResult = navigator.pushForResult(shortcut.intent.toIntentKey())
                ?.data ?: return@runCatching
            val finalShortcut = extractShortcut(shortcutRequestResult)
            navigator.pop(key, finalShortcut)
        }.onFailure {
            it.printStackTrace()
            toaster(stringResource(R.string.es_failed_to_pick_shortcut, emptyList()))
        }
    }
}

/*
 * Copyright 2019 Manuel Wrage
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

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.key
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.layout.size
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ivianuu.essentials.store.onEachAction
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnItems
import com.ivianuu.essentials.ui.store.component1
import com.ivianuu.essentials.ui.store.component2
import com.ivianuu.essentials.ui.store.execute
import com.ivianuu.essentials.ui.store.rememberStore
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.essentials.util.startActivityForResult
import com.ivianuu.injekt.Reader

@Reader
@Composable
fun ShortcutPickerPage(title: String? = null) {
    val (state, dispatch) = rememberStore { shortcutPickerStore() }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    title ?: stringResource(R.string.es_title_shortcut_picker)
                )
            })
        }
    ) {
        ResourceLazyColumnItems(state.shortcuts) { shortcut ->
            Shortcut(
                info = shortcut,
                onClick = { dispatch(ShortcutPickerAction.ShortcutClicked(shortcut)) })
        }
    }
}

@Composable
private fun Shortcut(
    onClick: () -> Unit,
    info: Shortcut
) {
    key(info) {
        ListItem(
            leading = {
                Image(
                    modifier = Modifier.size(40.dp),
                    painter = ImagePainter(info.icon)
                )
            },
            title = { Text(info.name) },
            onClick = onClick
        )
    }
}

@Reader
private fun shortcutPickerStore() =
    store<ShortcutPickerState, ShortcutPickerAction>(ShortcutPickerState()) {
        execute(
            block = { getShortcuts() },
            reducer = { copy(shortcuts = it) }
        )

        onEachAction { action ->
            when (action) {
                is ShortcutPickerAction.ShortcutClicked -> {
                    try {
                        val shortcutRequestResult = startActivityForResult(action.shortcut.intent)
                            .data ?: return@onEachAction

                        val shortcut = extractShortcut(shortcutRequestResult)

                        navigator.popTop(result = shortcut)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toaster.toast(R.string.es_failed_to_pick_shortcut)
                    }
                }
            }.exhaustive
        }
    }


@Immutable
private data class ShortcutPickerState(
    val shortcuts: Resource<List<Shortcut>> = Idle
)

private sealed class ShortcutPickerAction {
    data class ShortcutClicked(val shortcut: Shortcut) : ShortcutPickerAction()
}

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ImagePainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerAction.PickShortcut
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding

@KeyUiBinding<ShortcutPickerKey>
@FunBinding
@Composable
fun ShortcutPickerPage(
    dispatch: DispatchAction<ShortcutPickerAction>,
    state: @UiState ShortcutPickerState,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(R.string.es_title_shortcut_picker) }
            )
        }
    ) {
        ResourceLazyColumnFor(state.shortcuts) { shortcut ->
            Shortcut(
                info = shortcut,
                onClick = { dispatch(PickShortcut(shortcut)) }
            )
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

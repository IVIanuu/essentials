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

package com.ivianuu.essentials.twilight

import androidx.compose.Composable
import androidx.compose.Pivotal
import androidx.ui.material.RadioButton
import androidx.ui.res.stringResource
import com.ivianuu.essentials.ui.box.unfoldBox
import com.ivianuu.essentials.ui.common.ListScreen
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Route

val TwilightSettingsRoute = Route {
    val prefs = inject<TwilightPrefs>()

    ListScreen(title = stringResource(R.string.es_twilight_title)) {
        var twilightMode by unfoldBox(prefs.twilightMode)
        TwilightMode.values().toList().forEach { mode ->
            TwilightModeItem(
                mode = mode,
                isSelected = twilightMode == mode,
                onClick = { twilightMode = mode }
            )
        }
    }
}

@Composable
private fun TwilightModeItem(
    @Pivotal mode: TwilightMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        title = { Text(stringResource(mode.titleRes)) },
        subtitle = { Text(stringResource(mode.descRes)) },
        trailing = {
            RadioButton(
                selected = isSelected,
                onSelect = onClick
            )
        },
        onClick = onClick
    )
}

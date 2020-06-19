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
import androidx.compose.getValue
import androidx.compose.key
import androidx.compose.setValue
import androidx.ui.foundation.VerticalScroller
import androidx.ui.material.RadioButton
import com.ivianuu.essentials.ui.box.asState
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.injekt.Transient

@Transient
class TwilightSettingsPage(
    private val prefs: TwilightPrefs
) {
    @Composable
    operator fun invoke() {
        Scaffold(
            topBar = { TopAppBar(title = { Text(R.string.es_twilight_title) }) }
        ) {
            VerticalScroller {
                var twilightMode by prefs.twilightMode.asState()
                TwilightMode.values().toList().forEach { mode ->
                    TwilightModeItem(
                        mode = mode,
                        isSelected = twilightMode == mode,
                        onClick = { twilightMode = mode }
                    )
                }

                Subheader { Text(R.string.es_twilight_pref_category_more) }

                CheckboxListItem(
                    box = prefs.useBlack,
                    title = { Text(R.string.es_twilight_use_black) }
                )
            }
        }
    }
}

@Composable
private fun TwilightModeItem(
    mode: TwilightMode,
    onClick: () -> Unit,
    isSelected: Boolean
) {
    key(mode) {
        ListItem(
            title = { Text(mode.titleRes) },
            subtitle = { Text(mode.descRes) },
            trailing = {
                RadioButton(
                    selected = isSelected,
                    onSelect = onClick
                )
            },
            onClick = onClick
        )
    }
}

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

package com.ivianuu.essentials.twilight.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.android.prefs.PrefAction
import com.ivianuu.essentials.android.prefs.dispatchUpdate
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.twilight.R
import com.ivianuu.essentials.twilight.data.TwilightMode
import com.ivianuu.essentials.twilight.data.TwilightPrefs
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow

class TwilightSettingsKey : Key<Nothing>

@Given
val twilightSettingsKeyModule = KeyModule<TwilightSettingsKey>()

@Given
fun twilightSettingsUi(
    @Given prefsFlow: Flow<TwilightPrefs>,
    @Given prefUpdater: Collector<PrefAction<TwilightPrefs>>,
): KeyUi<TwilightSettingsKey> = {
    val prefs by prefsFlow.collectAsState(remember { TwilightPrefs() })
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_twilight_title)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            items(TwilightMode.values()) { mode ->
                TwilightModeItem(
                    mode = mode,
                    isSelected = prefs.twilightMode == mode,
                    onClick = {
                        prefUpdater.dispatchUpdate { copy(twilightMode = mode) }
                    }
                )
            }
            item {
                Subheader { Text(stringResource(R.string.es_twilight_pref_category_more)) }
            }
            item {
                CheckboxListItem(
                    value = prefs.useBlackInDarkMode,
                    onValueChange = {
                        prefUpdater.dispatchUpdate { copy(useBlackInDarkMode = it) }
                    },
                    title = { Text(stringResource(R.string.es_twilight_use_black)) }
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
    ListItem(
        title = { Text(stringResource(mode.titleRes)) },
        subtitle = { Text(stringResource(mode.descRes)) },
        trailing = {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        },
        onClick = onClick
    )
}

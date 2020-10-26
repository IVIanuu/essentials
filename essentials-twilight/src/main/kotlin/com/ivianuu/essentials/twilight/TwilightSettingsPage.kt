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

package com.ivianuu.essentials.twilight

import androidx.compose.material.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.ivianuu.essentials.store.setStateIn
import com.ivianuu.essentials.store.storeProvider
import com.ivianuu.essentials.twilight.TwilightSettingsAction.UpdateTwilightMode
import com.ivianuu.essentials.twilight.TwilightSettingsAction.UpdateUseBlackInDarkMode
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.essentials.ui.store.component1
import com.ivianuu.essentials.ui.store.component2
import com.ivianuu.essentials.ui.store.rememberStore
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding

@FunBinding
@Composable
fun TwilightSettingsPage(store: rememberStore<TwilightSettingsState, TwilightSettingsAction>) {
    val (state, dispatch) = store()
    Scaffold(
        topBar = { TopAppBar(title = { Text(R.string.es_twilight_title) }) }
    ) {
        InsettingScrollableColumn {
            TwilightMode.values().toList().forEach { mode ->
                TwilightModeItem(
                    mode = mode,
                    isSelected = state.twilightMode == mode,
                    onClick = { dispatch(UpdateTwilightMode(mode)) }
                )
            }

            Subheader { Text(R.string.es_twilight_pref_category_more) }

            CheckboxListItem(
                value = state.useBlackInDarkMode,
                onValueChange = { dispatch(UpdateUseBlackInDarkMode(it)) },
                title = { Text(R.string.es_twilight_use_black) }
            )
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
                    onClick = onClick
                )
            },
            onClick = onClick
        )
    }
}

@Binding
fun twilightSettingsStore(
    twilightPrefs: twilightPrefs,
    updateTwilightMode: updateTwilightMode,
    updateUseBlackInDarkMode: updateUseBlackInDarkMode
) = storeProvider<TwilightSettingsState, TwilightSettingsAction>(TwilightSettingsState()) {
    twilightPrefs.setStateIn(this) {
        copy(twilightMode = it.twilightMode, useBlackInDarkMode = it.useBlackInDarkMode)
    }

    for (action in this) {
        when (action) {
            is UpdateTwilightMode -> updateTwilightMode(action.mode)
            is UpdateUseBlackInDarkMode -> updateUseBlackInDarkMode(action.useBlackInDarkMode)
        }.exhaustive
    }
}

data class TwilightSettingsState(
    val twilightMode: TwilightMode = TwilightMode.System,
    val useBlackInDarkMode: Boolean = false
)

sealed class TwilightSettingsAction {
    data class UpdateTwilightMode(val mode: TwilightMode) : TwilightSettingsAction()
    data class UpdateUseBlackInDarkMode(val useBlackInDarkMode: Boolean) : TwilightSettingsAction()
}

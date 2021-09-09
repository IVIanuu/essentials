/*
 * Copyright 2021 Manuel Wrage
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
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.twilight.R
import com.ivianuu.essentials.twilight.data.TwilightMode
import com.ivianuu.essentials.twilight.data.TwilightPrefs
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.InjektCoroutineScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow

object TwilightSettingsKey : Key<Unit>

@Provide val twilightSettingsUi: ModelKeyUi<TwilightSettingsKey, TwilightSettingsModel> = {
  Scaffold(
    topBar = { TopAppBar(title = { Text(R.string.es_twilight_title) }) }
  ) {
    LazyColumn(contentPadding = localVerticalInsetsPadding()) {
      items(TwilightMode.values()) { mode ->
        TwilightModeItem(
          mode = mode,
          isSelected = model.twilightMode == mode,
          onClick = { model.updateTwilightMode(mode) }
        )
      }
      item {
        Subheader { Text(R.string.es_twilight_pref_category_more) }
      }
      item {
        CheckboxListItem(
          value = model.useBlackInDarkMode,
          onValueChange = model.updateUseBlackInDarkMode,
          title = { Text(R.string.es_twilight_use_black) }
        )
      }
    }
  }
}

@Composable private fun TwilightModeItem(
  mode: TwilightMode,
  onClick: () -> Unit,
  isSelected: Boolean
) {
  ListItem(
    title = {
      Text(
        stringResource(
          when (mode) {
            TwilightMode.SYSTEM -> R.string.es_twilight_mode_system
            TwilightMode.LIGHT -> R.string.es_twilight_mode_light
            TwilightMode.DARK -> R.string.es_twilight_mode_dark
            TwilightMode.BATTERY -> R.string.es_twilight_mode_battery
            TwilightMode.TIME -> R.string.es_twilight_mode_time
          }
        )
      )
    },
    subtitle = {
      Text(
        stringResource(
          when (mode) {
            TwilightMode.SYSTEM -> R.string.es_twilight_mode_system_desc
            TwilightMode.LIGHT -> R.string.es_twilight_mode_light_desc
            TwilightMode.DARK -> R.string.es_twilight_mode_dark_desc
            TwilightMode.BATTERY -> R.string.es_twilight_mode_battery_desc
            TwilightMode.TIME -> R.string.es_twilight_mode_time_desc
          }
        )
      )
    },
    trailing = {
      RadioButton(
        selected = isSelected,
        onClick = null
      )
    },
    onClick = onClick
  )
}


@Optics data class TwilightSettingsModel(
  val twilightMode: TwilightMode = TwilightMode.SYSTEM,
  val useBlackInDarkMode: Boolean = false,
  val updateTwilightMode: (TwilightMode) -> Unit = {},
  val updateUseBlackInDarkMode: (Boolean) -> Unit = {}
)

@Provide fun twilightSettingsModel(
  pref: DataStore<TwilightPrefs>,
  scope: InjektCoroutineScope<KeyUiScope>
): @Scoped<KeyUiScope> StateFlow<TwilightSettingsModel> =
  scope.state(TwilightSettingsModel()) {
    pref.data.update {
      copy(
        twilightMode = it.twilightMode,
        useBlackInDarkMode = it.useBlackInDarkMode
      )
    }
    action(TwilightSettingsModel.updateTwilightMode()) { value ->
      pref.updateData { copy(twilightMode = value) }
    }
    action(TwilightSettingsModel.updateUseBlackInDarkMode()) { value ->
      pref.updateData { copy(useBlackInDarkMode = value) }
    }
  }

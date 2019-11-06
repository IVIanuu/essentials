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
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.material.RadioButton
import androidx.ui.res.stringResource
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.common.SimpleListItem
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.prefs.PrefsScreen
import com.ivianuu.essentials.ui.navigation.director.defaultControllerRouteOptionsOrNull

val twilightSettingsRoute = composeControllerRoute(
    options = defaultControllerRouteOptionsOrNull()
) {
    PrefsScreen(
        appBar = { EsTopAppBar(+stringResource(R.string.es_title_twilight)) },
        prefs = {
            val prefs = +inject<TwilightPrefs>()
            TwilightMode.values().forEach { mode ->
                TwilightModeItem(
                    mode = mode,
                    isSelected = prefs.twilightMode.get() == mode,
                    onClick = { prefs.twilightMode.set(mode) }
                )
            }
        }
    )
}

@Composable
private fun TwilightModeItem(
    mode: TwilightMode,
    isSelected: Boolean,
    onClick: () -> Unit
) = composable(mode.value, isSelected) {
    SimpleListItem(
        title = { Text(+stringResource(mode.titleRes)) },
        subtitle = { Text(+stringResource(mode.descRes)) },
        trailing = {
            RadioButton(
                selected = isSelected,
                onSelect = onClick
            )
        },
        onClick = onClick
    )
}
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

package com.ivianuu.essentials.apps.ui.checkableapps

import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsAction.DeselectAll
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsAction.SelectAll
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsAction.UpdateAppCheckState
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.injekt.Given
import dev.chrisbanes.accompanist.coil.CoilImage

typealias CheckableAppsScreen = @Composable () -> Unit

@Given
fun checkableAppsScreen(
    @Given stateProvider: @Composable () -> @UiState CheckableAppsState,
    @Given dispatch: DispatchAction<CheckableAppsAction>,
): CheckableAppsScreen = {
    val state = stateProvider()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.appBarTitle) },
                actions = {
                    PopupMenuButton(
                        items = listOf(
                            PopupMenu.Item(
                                onSelected = {
                                    dispatch(SelectAll)
                                }
                            ) {
                                Text(R.string.es_select_all)
                            },
                            PopupMenu.Item(
                                onSelected = {
                                    dispatch(DeselectAll)
                                }
                            ) {
                                Text(R.string.es_deselect_all)
                            }
                        )
                    )
                }
            )
        }
    ) {
        ResourceLazyColumnFor(state.checkableApps) { app ->
            CheckableApp(
                app = app,
                onClick = { dispatch(UpdateAppCheckState(app, !app.isChecked)) }
            )
        }
    }
}

@Composable
private fun CheckableApp(
    onClick: () -> Unit,
    app: CheckableApp
) {
    ListItem(
        title = { Text(app.info.appName) },
        leading = {
            CoilImage(
                data = AppIcon(packageName = app.info.packageName),
                modifier = Modifier.size(40.dp),
                contentDescription = null
            )
        },
        trailing = {
            Checkbox(
                checked = app.isChecked,
                onCheckedChange = { onClick() }
            )
        },
        onClick = onClick
    )
}

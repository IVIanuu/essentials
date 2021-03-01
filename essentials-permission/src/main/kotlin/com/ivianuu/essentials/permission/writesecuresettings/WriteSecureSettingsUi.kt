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

package com.ivianuu.essentials.permission.writesecuresettings

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsAction.GrantPermissionsViaRoot
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsAction.OpenPcInstructions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.core.localVerticalInsets
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.injekt.Given

@Given
fun writeSecureSettingsUi(
    @Given dispatch: DispatchAction<WriteSecureSettingsAction>,
    @Given stateProvider: @Composable () -> @UiState WriteSecureSettingsState,
): KeyUi<WriteSecureSettingsKey> = {
    val state = stateProvider()
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_title_secure_settings)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsets()) {
            item {
                SecureSettingsHeader(
                    stringResource(R.string.es_pref_secure_settings_header_summary)
                )

                ListItem(
                    title = { Text(stringResource(R.string.es_pref_use_pc)) },
                    subtitle = { Text(stringResource(R.string.es_pref_use_pc_summary)) },
                    onClick = { dispatch(OpenPcInstructions) }
                )

                ListItem(
                    title = { Text(stringResource(R.string.es_pref_use_root)) },
                    subtitle = { Text(stringResource(R.string.es_pref_use_root_summary)) },
                    onClick = { dispatch(GrantPermissionsViaRoot) }
                )
            }
        }
    }
}

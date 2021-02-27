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
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.CopyAdbCommand
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.OpenGadgetHacksTutorial
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.OpenLifeHackerTutorial
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.OpenXdaTutorial
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.core.Icon
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.localVerticalInsets
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.injekt.Given

@KeyUiBinding<WriteSecureSettingsPcInstructionsKey>
@Given
fun writeSecureSettingsPcInstructionsKeyUi(
    @Given stateProvider: @Composable () -> @UiState WriteSecureSettingsPcInstructionsState,
    @Given dispatch: DispatchAction<WriteSecureSettingsPcInstructionsAction>,
): KeyUi = {
    val state = stateProvider()
    Scaffold(
        topBar = { TopAppBar(title = { Text(R.string.es_title_secure_settings_pc_instructions) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsets()) {
            item {
                SecureSettingsHeader(
                    text = stringResource(R.string.es_pref_secure_settings_pc_instructions_header_summary)
                )
                ListItem(
                    title = { Text(R.string.es_pref_secure_settings_step_1) },
                    subtitle = { Text(R.string.es_pref_secure_settings_step_1_summary) }
                )

                ListItem(
                    title = { Text(R.string.es_pref_secure_settings_step_2) },
                    subtitle = { Text(R.string.es_pref_secure_settings_step_2_summary) }
                )

                ListItem(
                    title = { Text(R.string.es_pref_secure_settings_step_3) },
                    subtitle = { Text(R.string.es_pref_secure_settings_step_3_summary) }
                )

                ListItem(
                    leading = { Icon(R.drawable.es_ic_link, null) },
                    title = { Text(R.string.es_pref_secure_settings_link_gadget_hacks_summary) },
                    onClick = { dispatch(OpenGadgetHacksTutorial) }
                )

                ListItem(
                    leading = { Icon(R.drawable.es_ic_link, null) },
                    title = { Text(R.string.es_pref_secure_settings_link_lifehacker_summary) },
                    onClick = { dispatch(OpenLifeHackerTutorial) }
                )

                ListItem(
                    leading = { Icon(R.drawable.es_ic_link, null) },
                    title = { Text(R.string.es_pref_secure_settings_link_xda_summary) },
                    onClick = { dispatch(OpenXdaTutorial) }
                )

                ListItem(
                    title = { Text(R.string.es_pref_secure_settings_step_4) },
                    subtitle = {
                        Text(
                            stringResource(
                                R.string.es_pref_secure_settings_step_4_summary,
                                state.packageName
                            )
                        )
                    },
                    onClick = { dispatch(CopyAdbCommand) }
                )
            }
        }
    }
}

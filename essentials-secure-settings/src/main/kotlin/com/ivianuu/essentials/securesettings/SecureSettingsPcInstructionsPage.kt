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

package com.ivianuu.essentials.securesettings

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.ivianuu.essentials.securesettings.SecureSettingsPcInstructionsAction.ShowGadgetHacksTutorial
import com.ivianuu.essentials.securesettings.SecureSettingsPcInstructionsAction.ShowLifeHackerTutorial
import com.ivianuu.essentials.securesettings.SecureSettingsPcInstructionsAction.ShowXdaTutorial
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.prefs.ClipboardListItem
import com.ivianuu.essentials.ui.store.Dispatch
import com.ivianuu.essentials.ui.store.State
import com.ivianuu.injekt.FunBinding

@FunBinding
@Composable
fun SecureSettingsPcInstructionsPage(
    state: @State SecureSettingsPcInstructionsState,
    dispatch: @Dispatch (SecureSettingsPcInstructionsAction) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(R.string.es_title_secure_settings_pc_instructions) }) }
    ) {
        InsettingScrollableColumn {
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
                leading = { Icon(vectorResource(R.drawable.es_ic_link)) },
                title = { Text(R.string.es_pref_secure_settings_link_gadget_hacks_summary) },
                onClick = { dispatch(ShowGadgetHacksTutorial) }
            )

            ListItem(
                leading = { Icon(vectorResource(R.drawable.es_ic_link)) },
                title = { Text(R.string.es_pref_secure_settings_link_lifehacker_summary) },
                onClick = { dispatch(ShowLifeHackerTutorial) }
            )

            ListItem(
                leading = { Icon(vectorResource(R.drawable.es_ic_link)) },
                title = { Text(R.string.es_pref_secure_settings_link_xda_summary) },
                onClick = { dispatch(ShowXdaTutorial) }
            )

            ClipboardListItem(
                title = { Text(R.string.es_pref_secure_settings_step_4) },
                subtitle = {
                    Text(
                        stringResource(
                            R.string.es_pref_secure_settings_step_4_summary,
                            state.packageName
                        )
                    )
                },
                clipboardText = { state.secureSettingsAdbCommand }
            )
        }
    }
}

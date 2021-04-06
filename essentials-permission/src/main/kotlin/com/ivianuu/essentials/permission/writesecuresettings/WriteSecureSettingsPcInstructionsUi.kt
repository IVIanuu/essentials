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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.clipboard.ClipboardAction
import com.ivianuu.essentials.clipboard.ClipboardAction.UpdateClipboard
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.*
import com.ivianuu.essentials.store.Sink
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.onAction
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive

class WriteSecureSettingsPcInstructionsKey(
    val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Nothing>

@Given
val writeSecureSettingsPcInstructionsUi: StoreKeyUi<WriteSecureSettingsPcInstructionsKey,
        WriteSecureSettingsPcInstructionsState,
        WriteSecureSettingsPcInstructionsAction> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_title_secure_settings_pc_instructions)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                SecureSettingsHeader(
                    text = stringResource(R.string.es_pref_secure_settings_pc_instructions_header_summary)
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_secure_settings_step_1)) },
                    subtitle = { Text(stringResource(R.string.es_pref_secure_settings_step_1_summary)) }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_secure_settings_step_2)) },
                    subtitle = { Text(stringResource(R.string.es_pref_secure_settings_step_2_summary)) }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_secure_settings_step_3)) },
                    subtitle = { Text(stringResource(R.string.es_pref_secure_settings_step_3_summary)) }
                )
            }
            item {
                ListItem(
                    leading = { Icon(painterResource(R.drawable.es_ic_link), null) },
                    title = { Text(stringResource(R.string.es_pref_secure_settings_link_gadget_hacks_summary)) },
                    onClick = { send(OpenGadgetHacksTutorial) }
                )
            }
            item {
                ListItem(
                    leading = { Icon(painterResource(R.drawable.es_ic_link), null) },
                    title = { Text(stringResource(R.string.es_pref_secure_settings_link_lifehacker_summary)) },
                    onClick = { send(OpenLifehackerTutorial) }
                )
            }
            item {
                ListItem(
                    leading = { Icon(painterResource(R.drawable.es_ic_link), null) },
                    title = { Text(stringResource(R.string.es_pref_secure_settings_link_xda_summary)) },
                    onClick = { send(OpenXdaTutorial) }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_secure_settings_step_4)) },
                    subtitle = {
                        Text(
                            stringResource(
                                R.string.es_pref_secure_settings_step_4_summary,
                                state.packageName
                            )
                        )
                    },
                    onClick = { send(CopyAdbCommand) }
                )
            }
        }
    }
}

data class WriteSecureSettingsPcInstructionsState(val packageName: String) {
    val secureSettingsAdbCommand =
        "adb shell pm grant $packageName android.permission.WRITE_SECURE_SETTINGS"
    companion object {
        @Given
        fun initialR(@Given buildInfo: BuildInfo):
                @Initial WriteSecureSettingsPcInstructionsState =
            WriteSecureSettingsPcInstructionsState(packageName = buildInfo.packageName)
    }
}

sealed class WriteSecureSettingsPcInstructionsAction {
    object CopyAdbCommand : WriteSecureSettingsPcInstructionsAction()
    object OpenGadgetHacksTutorial : WriteSecureSettingsPcInstructionsAction()
    object OpenLifehackerTutorial : WriteSecureSettingsPcInstructionsAction()
    object OpenXdaTutorial : WriteSecureSettingsPcInstructionsAction()
}

@Given
fun writeSecureSettingsPcInstructionsStore(
    @Given navigator: Sink<NavigationAction>,
    @Given clipboard: Sink<ClipboardAction>,
    @Given key: WriteSecureSettingsPcInstructionsKey,
    @Given permissionStateFactory: PermissionStateFactory
): StoreBuilder<KeyUiGivenScope, WriteSecureSettingsPcInstructionsState,
        WriteSecureSettingsPcInstructionsAction> = {
    effect {
        val state = permissionStateFactory(listOf(key.permissionKey))
        while (coroutineContext.isActive) {
            if (state.first()) {
                navigator.pop(key)
                break
            }
            delay(200)
        }
    }
    onAction<CopyAdbCommand> {
        clipboard.send(UpdateClipboard(state.first().secureSettingsAdbCommand))
    }
    onAction<OpenGadgetHacksTutorial> {
        navigator.push(UrlKey("https://youtu.be/CDuxcrrWLnY"))
    }
    onAction<OpenLifehackerTutorial> {
        navigator.push(
            UrlKey("https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378")
        )
    }
    onAction<OpenXdaTutorial> {
        navigator.push(
            UrlKey("https://www.xda-developers.com/install-adb-windows-macos-linux/")
        )
    }
}

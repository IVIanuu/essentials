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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.clipboard.ClipboardAction
import com.ivianuu.essentials.clipboard.ClipboardAction.UpdateClipboard
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.CopyAdbCommand
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.OpenGadgetHacksTutorial
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.OpenLifeHackerTutorial
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPcInstructionsAction.OpenXdaTutorial
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.currentState
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WriteSecureSettingsPcInstructionsKey(
    val permissionKey: TypeKey<WriteSecureSettingsPermission>
) : Key<Nothing>

@Given
val writeSecureSettingsPcInstructionsKeyModule = KeyModule<WriteSecureSettingsPcInstructionsKey>()

@Given
fun writeSecureSettingsPcInstructionsUi(
    @Given stateFlow: StateFlow<WriteSecureSettingsPcInstructionsState>,
    @Given dispatch: Collector<WriteSecureSettingsPcInstructionsAction>,
): KeyUi<WriteSecureSettingsPcInstructionsKey> = {
    val state by stateFlow.collectAsState()
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
                    onClick = { dispatch(OpenGadgetHacksTutorial) }
                )
            }
            item {
                ListItem(
                    leading = { Icon(painterResource(R.drawable.es_ic_link), null) },
                    title = { Text(stringResource(R.string.es_pref_secure_settings_link_lifehacker_summary)) },
                    onClick = { dispatch(OpenLifeHackerTutorial) }
                )
            }
            item {
                ListItem(
                    leading = { Icon(painterResource(R.drawable.es_ic_link), null) },
                    title = { Text(stringResource(R.string.es_pref_secure_settings_link_xda_summary)) },
                    onClick = { dispatch(OpenXdaTutorial) }
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
                    onClick = { dispatch(CopyAdbCommand) }
                )
            }
        }
    }
}

data class WriteSecureSettingsPcInstructionsState(val packageName: String) {
    val secureSettingsAdbCommand =
        "adb shell pm grant $packageName android.permission.WRITE_SECURE_SETTINGS"
}

@Given
fun initialWriteSecureSettingsPcInstructionsState(@Given buildInfo: BuildInfo):
        @Initial WriteSecureSettingsPcInstructionsState =
    WriteSecureSettingsPcInstructionsState(packageName = buildInfo.packageName)

sealed class WriteSecureSettingsPcInstructionsAction {
    object CopyAdbCommand : WriteSecureSettingsPcInstructionsAction()
    object OpenGadgetHacksTutorial : WriteSecureSettingsPcInstructionsAction()
    object OpenLifeHackerTutorial : WriteSecureSettingsPcInstructionsAction()
    object OpenXdaTutorial : WriteSecureSettingsPcInstructionsAction()
}

@Given
fun writeSecureSettingsPcInstructionsState(
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given initial: @Initial WriteSecureSettingsPcInstructionsState,
    @Given actions: Flow<WriteSecureSettingsPcInstructionsAction>,
    @Given navigator: Collector<NavigationAction>,
    @Given clipboard: Collector<ClipboardAction>,
    @Given key: WriteSecureSettingsPcInstructionsKey,
    @Given permissionStateFactory: PermissionStateFactory
): @Scoped<KeyUiGivenScope> StateFlow<WriteSecureSettingsPcInstructionsState> = scope.state(initial) {
    launch {
        val state = permissionStateFactory(listOf(key.permissionKey))
        while (coroutineContext.isActive) {
            if (state.first()) {
                navigator(Pop(key))
                break
            }
            delay(200)
        }
    }

    actions
        .filterIsInstance<CopyAdbCommand>()
        .onEach { clipboard(UpdateClipboard(currentState().secureSettingsAdbCommand)) }
        .launchIn(this)

    actions
        .filterIsInstance<OpenGadgetHacksTutorial>()
        .onEach {
            navigator(
                Push(
                    UrlKey("https://youtu.be/CDuxcrrWLnY")
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenLifeHackerTutorial>()
        .onEach {
            navigator(
                Push(
                    UrlKey("https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378")
                )
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<OpenXdaTutorial>()
        .onEach {
            navigator(
                Push(
                    UrlKey("https://www.xda-developers.com/install-adb-windows-macos-linux/")
                )
            )
        }
        .launchIn(this)
}

@Given
val writeSecureSettingsPcInstructionsActions:
        @Scoped<KeyUiGivenScope> MutableSharedFlow<WriteSecureSettingsPcInstructionsAction>
    get() = EventFlow()
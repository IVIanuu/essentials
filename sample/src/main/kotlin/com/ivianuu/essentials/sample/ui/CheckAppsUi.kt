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

package com.ivianuu.essentials.sample.ui

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.essentials.apps.ui.LaunchableAppFilter
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Given
val checkAppsHomeItem = HomeItem("Check apps") { CheckAppsKey() }

class CheckAppsKey : Key<Nothing>

@Given
fun checkAppsUi(
    @Given checkableAppsScreen: (@Given CheckableAppsParams) -> CheckableAppsScreen,
    @Given launchableAppFilter: LaunchableAppFilter,
    @Given pref: DataStore<CheckAppsPrefs>
): KeyUi<CheckAppsKey> = {
    val scope = rememberCoroutineScope()
    remember {
        checkableAppsScreen(
            CheckableAppsParams(
                pref.data.map { it.checkedApps },
                { checkedApps ->
                    scope.launch {
                        pref.updateData {
                            copy(checkedApps = checkedApps)
                        }
                    }
                },
                launchableAppFilter,
                "Send check apps"
            )
        )
    }.invoke()
}

@Serializable
data class CheckAppsPrefs(
    @SerialName("checked_apps") val checkedApps: Set<String> = emptySet(),
)

@Given
val checkAppsPrefsModule = PrefModule<CheckAppsPrefs>("check_apps_prefs")

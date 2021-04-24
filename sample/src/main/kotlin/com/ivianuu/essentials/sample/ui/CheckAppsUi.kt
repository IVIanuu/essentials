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

import androidx.compose.runtime.*
import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.checkableapps.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

@Given
val checkAppsHomeItem = HomeItem("Check apps") { CheckAppsKey }

object CheckAppsKey : Key<Nothing>

@Given
fun checkAppsUi(
    @Given checkableAppsScreen: (@Given CheckableAppsParams) -> CheckableAppsScreen,
    @Given launchableAppPredicate: LaunchableAppPredicate,
    @Given pref: DataStore<CheckAppsPrefs>,
    @Given scope: GivenCoroutineScope<KeyUiGivenScope>
): KeyUi<CheckAppsKey> = {
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
                launchableAppPredicate,
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
val checkAppsPrefsModule = PrefModule("check_apps_prefs") { CheckAppsPrefs() }

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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.ivianuu.essentials.apps.ui.LaunchableAppFilter
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.datastore.android.PrefBinding
import com.ivianuu.essentials.datastore.android.updatePref
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.injekt.FunBinding
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HomeItemBinding("Check apps")
class CheckAppsKey

@KeyUiBinding<CheckAppsKey>
@FunBinding
@Composable
fun CheckAppsScreen(
    checkableAppsScreen: (CheckableAppsParams) -> CheckableAppsScreen,
    launchableAppFilter: LaunchableAppFilter,
    prefs: Flow<CheckAppsPrefs>,
    updatePrefs: updatePref<CheckAppsPrefs>,
) {
    remember {
        checkableAppsScreen(
            CheckableAppsParams(
                prefs.map { it.checkedApps },
                { checkedApps ->
                    updatePrefs {
                        copy(checkedApps = checkedApps)
                    }
                },
                launchableAppFilter,
                "Send check apps"
            )
        )
    }()
}

@PrefBinding("check_apps_prefs")
@JsonClass(generateAdapter = true)
data class CheckAppsPrefs(
    @Json(name = "checked_apps") val checkedApps: Set<String> = emptySet(),
)

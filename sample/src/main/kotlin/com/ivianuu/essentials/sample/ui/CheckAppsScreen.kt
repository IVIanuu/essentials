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
import com.ivianuu.essentials.datastore.disk.DiskDataStoreFactory
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.launch

@HomeItemBinding("Check apps")
class CheckAppsKey

@KeyUiBinding<CheckAppsKey>
@FunBinding
@Composable
fun CheckAppsScreen(
    checkableAppsScreen: (CheckableAppsParams) -> CheckableAppsScreen,
    factory: DiskDataStoreFactory,
    launchableAppFilter: LaunchableAppFilter,
) {
    val checkedAppsStore = remember { factory.create("checked_apps") { emptySet<String>() } }
    val scope = rememberCoroutineScope()
    remember {
        checkableAppsScreen(
            CheckableAppsParams(
                checkedAppsStore.data,
                { checkedApps ->
                    scope.launch {
                        checkedAppsStore.updateData { checkedApps }
                    }
                },
                launchableAppFilter,
                "Send check apps"
            )
        )
    }()
}

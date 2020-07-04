/*
 * Copyright 2019 Manuel Wrage
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

import androidx.compose.Composable
import androidx.compose.remember
import com.ivianuu.essentials.apps.ui.CheckableAppsPage
import com.ivianuu.essentials.apps.ui.LaunchableAppFilter
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.injekt.Unscoped

@Unscoped
class CheckAppsPage(
    private val boxFactory: DiskDataStoreFactory,
    private val checkableAppsPage: CheckableAppsPage,
    private val launchableAppFilter: LaunchableAppFilter
) {
    @Composable
    operator fun invoke() {
        val box = remember { boxFactory.create("apps") { emptySet<String>() } }
        checkableAppsPage(
            checkedApps = box.data,
            onCheckedAppsChanged = { newValue ->
                box.updateData { newValue }
            },
            appBarTitle = "Send check apps",
            appFilter = launchableAppFilter
        )
    }
}

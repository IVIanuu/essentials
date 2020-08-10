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
import androidx.compose.getValue
import androidx.compose.remember
import androidx.compose.setValue
import com.ivianuu.essentials.apps.ui.CheckableAppsPage
import com.ivianuu.essentials.apps.ui.LaunchableAppFilter
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.ui.datastore.asState
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@Reader
@Composable
fun CheckAppsPage() {
    var checkedApps by remember {
        given<DiskDataStoreFactory>().create("checked_apps") { emptySet<String>() }
    }.asState()
    CheckableAppsPage(
        checkedApps = checkedApps,
        onCheckedAppsChanged = { newCheckedApps -> checkedApps = newCheckedApps },
        appBarTitle = "Send check apps",
        appFilter = remember { given<LaunchableAppFilter>() }
    )
}

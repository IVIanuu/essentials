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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.apps.ui.CheckableAppsPage
import com.ivianuu.essentials.apps.ui.LaunchableAppFilter
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.android.asState
import com.ivianuu.injekt.Binding

typealias CheckAppsPage = @Composable () -> Unit

@Binding
fun CheckAppsPage(
    checkableAppsPage: CheckableAppsPage,
    factory: DiskDataStoreFactory,
    launchableAppFilter: LaunchableAppFilter,
): CheckAppsPage = {
    var checkedApps by remember {
        factory.create("checked_apps") { emptySet<String>() }
    }.asState()
    checkableAppsPage(
        checkedApps,
        { newCheckedApps -> checkedApps = newCheckedApps },
        launchableAppFilter,
        "Send check apps"
    )
}

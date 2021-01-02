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

package com.ivianuu.essentials.apps.ui.apppicker

import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.ui.AppFilter
import com.ivianuu.essentials.apps.ui.DefaultAppFilter
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.map
import com.ivianuu.injekt.Given

data class AppPickerKey(
    val appFilter: AppFilter = DefaultAppFilter,
    val title: String? = null,
)

data class AppPickerState(
    private val allApps: Resource<List<AppInfo>> = Idle,
    val appFilter: AppFilter = DefaultAppFilter,
    val title: String? = null
) {
    val filteredApps = allApps
        .map { it.filter(appFilter) }
    companion object {
        @Given
        fun initial(@Given params: AppPickerKey): @Initial AppPickerState = AppPickerState(
            appFilter = params.appFilter,
            title = params.title
        )
    }
}

sealed class AppPickerAction {
    data class FilterApps(val appFilter: AppFilter) : AppPickerAction()
    data class PickApp(val app: AppInfo) : AppPickerAction()
}

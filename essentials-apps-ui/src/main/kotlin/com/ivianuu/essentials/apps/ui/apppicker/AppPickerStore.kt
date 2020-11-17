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

import com.ivianuu.essentials.apps.getInstalledApps
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerAction.*
import com.ivianuu.essentials.store.iterator
import com.ivianuu.essentials.store.reduce
import com.ivianuu.essentials.store.reduceIn
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.popTop
import com.ivianuu.essentials.ui.resource.resource
import com.ivianuu.essentials.ui.resource.resourceFlow
import com.ivianuu.essentials.ui.store.Initial
import com.ivianuu.essentials.ui.store.UiStoreBinding
import com.ivianuu.essentials.ui.store.reduceResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@UiStoreBinding
fun CoroutineScope.AppPickerStore(
    getInstalledApps: getInstalledApps,
    initial: @Initial AppPickerState,
    navigator: Navigator
) = store<AppPickerState, AppPickerAction>(initial) {
    reduceResource({ getInstalledApps() }) { copy(allApps = it) }
    for (action in this) {
        when (action) {
            is FilterApps -> reduce { copy(appFilter = action.appFilter) }
            is PickApp -> navigator.popTop(result = action.app)
        }
    }
}

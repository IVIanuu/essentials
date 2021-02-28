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

import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerAction.FilterApps
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerAction.PickApp
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.resource.reduceResource
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.injekt.Given
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@UiStateBinding
@Given
fun appPickerState(
    @Given scope: CoroutineScope,
    @Given initial: @Initial AppPickerState,
    @Given actions: Actions<AppPickerAction>,
    @Given appRepository: AppRepository,
    @Given key: AppPickerKey,
    @Given navigator: DispatchAction<NavigationAction>,
): StateFlow<AppPickerState> = scope.state(initial) {
    reduceResource({ appRepository.getInstalledApps() }) { copy(allApps = it) }
    actions
        .filterIsInstance<FilterApps>()
        .reduce { copy(appFilter = it.appFilter) }
        .launchIn(this)
    actions
        .filterIsInstance<PickApp>()
        .onEach { navigator(Pop(key, it.app)) }
        .launchIn(this)
}

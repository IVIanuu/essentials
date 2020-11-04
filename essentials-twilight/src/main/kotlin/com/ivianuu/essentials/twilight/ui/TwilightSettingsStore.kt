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

package com.ivianuu.essentials.twilight.ui

import com.ivianuu.essentials.store.setStateIn
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.twilight.data.TwilightModePref
import com.ivianuu.essentials.twilight.data.UseBlackInDarkModePref
import com.ivianuu.essentials.twilight.ui.TwilightSettingsAction.UpdateTwilightMode
import com.ivianuu.essentials.twilight.ui.TwilightSettingsAction.UpdateUseBlackInDarkMode
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.Binding
import kotlinx.coroutines.CoroutineScope

@Binding
fun CoroutineScope.TwilightSettingsStore(
    twilightModePref: TwilightModePref,
    useBlackInDarkModePref: UseBlackInDarkModePref
) = store<TwilightSettingsState, TwilightSettingsAction>(TwilightSettingsState()) {
    twilightModePref.data.setStateIn(this) { copy(twilightMode = it) }
    useBlackInDarkModePref.data.setStateIn(this) { copy(useBlackInDarkMode = it) }
    for (action in this) {
        when (action) {
            is UpdateTwilightMode -> twilightModePref.updateData { action.value }
            is UpdateUseBlackInDarkMode -> useBlackInDarkModePref.updateData { action.value }
        }.exhaustive
    }
}

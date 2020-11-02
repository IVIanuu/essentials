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
import com.ivianuu.essentials.store.storeProvider
import com.ivianuu.essentials.twilight.domain.twilightPrefs
import com.ivianuu.essentials.twilight.domain.updateTwilightMode
import com.ivianuu.essentials.twilight.domain.updateUseBlackInDarkMode
import com.ivianuu.essentials.twilight.ui.TwilightSettingsAction.*
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.Binding

@Binding
fun twilightSettingsStore(
    twilightPrefs: twilightPrefs,
    updateTwilightMode: updateTwilightMode,
    updateUseBlackInDarkMode: updateUseBlackInDarkMode
) = storeProvider<TwilightSettingsState, TwilightSettingsAction>(TwilightSettingsState()) {
    twilightPrefs.setStateIn(this) {
        copy(twilightMode = it.twilightMode, useBlackInDarkMode = it.useBlackInDarkMode)
    }

    for (action in this) {
        when (action) {
            is UpdateTwilightMode -> updateTwilightMode(action.value)
            is UpdateUseBlackInDarkMode -> updateUseBlackInDarkMode(action.value)
        }.exhaustive
    }
}

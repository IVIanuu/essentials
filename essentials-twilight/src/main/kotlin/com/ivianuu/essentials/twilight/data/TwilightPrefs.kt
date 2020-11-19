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

package com.ivianuu.essentials.twilight.data

import com.ivianuu.essentials.data.store.invokePersistedState
import com.ivianuu.essentials.data.store.persistedState
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.twilight.data.TwilightPrefsAction.UpdateTwilightMode
import com.ivianuu.essentials.twilight.data.TwilightPrefsAction.UpdateUseBlackInDarkMode
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TwilightPrefsState(
    @Json(name = "twilight_mode") val twilightMode: TwilightMode = TwilightMode.System,
    @Json(name = "use_black_in_dark_mode") val useBlackInDarkMode: Boolean = false
)

sealed class TwilightPrefsAction {
    data class UpdateTwilightMode(val value: TwilightMode) : TwilightPrefsAction()
    data class UpdateUseBlackInDarkMode(val value: Boolean) : TwilightPrefsAction()
}

@Binding(ApplicationComponent::class)
fun TwilightPrefsStore(
    actions: Actions<TwilightPrefsAction>,
    persistedStore: persistedState<TwilightPrefsState>
) = persistedStore.invokePersistedState("twilight_prefs", TwilightPrefsState()) {
    actions.reduce { action ->
        when (action) {
            is UpdateTwilightMode -> copy(twilightMode = action.value)
            is UpdateUseBlackInDarkMode -> copy(useBlackInDarkMode = action.value)
        }
    }
}

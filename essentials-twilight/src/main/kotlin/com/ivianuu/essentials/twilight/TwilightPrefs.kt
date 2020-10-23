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

package com.ivianuu.essentials.twilight

import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.store.dataStore
import com.ivianuu.essentials.twilight.TwilightAction.ChangeTwilightMode
import com.ivianuu.essentials.twilight.TwilightAction.UseBlackInDarkModeChange
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TwilightPrefs(
    @Json(name = "twilight_mode") val twilightMode: TwilightMode = TwilightMode.System,
    @Json(name = "use_black_in_dark_mode") val useBlackInDarkMode: Boolean = false
)

typealias TwilightPrefsStore = Store<TwilightPrefs, TwilightAction>
@Binding(ApplicationComponent::class)
fun twilightPrefsStore(dataStore: dataStore<TwilightPrefs, TwilightAction>): TwilightPrefsStore =
    dataStore("twilight_prefs", TwilightPrefs()) {
        onEachAction { action ->
            when (action) {
                is ChangeTwilightMode -> setState { copy(twilightMode = action.newValue) }
                is UseBlackInDarkModeChange -> setState { copy(useBlackInDarkMode = action.newValue) }
            }.exhaustive
        }
    }

sealed class TwilightAction {
    data class ChangeTwilightMode(val newValue: TwilightMode) : TwilightAction()
    data class UseBlackInDarkModeChange(val newValue: Boolean) : TwilightAction()
}

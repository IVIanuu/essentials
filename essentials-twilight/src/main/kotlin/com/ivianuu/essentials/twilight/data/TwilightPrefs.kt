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

import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.disk.DiskDataStoreFactory
import com.ivianuu.essentials.store.iterator
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.twilight.data.TwilightPrefsAction.*
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent

data class TwilightPrefsState(
    val twilightMode: TwilightMode = TwilightMode.System,
    val useBlackInDarkMode: Boolean = false
)

sealed class TwilightPrefsAction {
    data class UpdateTwilightMode(val value: TwilightMode) : TwilightPrefsAction()
    data class UpdateUseBlackInDarkMode(val value: Boolean) : TwilightPrefsAction()
}

@Binding
fun TwilightPrefsStore(
    scope: GlobalScope
) = scope.store<TwilightPrefsState, TwilightPrefsAction>(
    TwilightPrefsState()
) {
    for (action in this) {
        when (action) {
            is UpdateTwilightMode -> setState { copy(twilightMode = action.value) }
            is UpdateUseBlackInDarkMode -> setState { copy(useBlackInDarkMode = action.value) }
        }
    }
}

typealias TwilightModePref = DataStore<TwilightMode>
@Binding(ApplicationComponent::class)
fun twilightModePref(factory: DiskDataStoreFactory): TwilightModePref =
    factory.create("twilight_mode") { TwilightMode.System }

typealias UseBlackInDarkModePref = DataStore<Boolean>
@Binding(ApplicationComponent::class)
fun useBlackInDarkModePref(factory: DiskDataStoreFactory): UseBlackInDarkModePref =
    factory.create("use_black_in_dark_mode_pref") { false }

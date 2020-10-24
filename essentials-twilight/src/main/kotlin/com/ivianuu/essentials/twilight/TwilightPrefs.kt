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

import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.Flow

@JsonClass(generateAdapter = true)
data class TwilightPrefs(
    @Json(name = "twilight_mode") val twilightMode: TwilightMode = TwilightMode.System,
    @Json(name = "use_black_in_dark_mode") val useBlackInDarkMode: Boolean = false
)

internal typealias TwilightPrefsStore = DataStore<TwilightPrefs>
@Binding(ApplicationComponent::class)
fun twilightPrefsStore(factory: DiskDataStoreFactory): TwilightPrefsStore =
    factory.create("twilight_prefs") { TwilightPrefs() }

typealias twilightPrefs = Flow<TwilightPrefs>
@Binding
fun twilightPrefs(store: TwilightPrefsStore): twilightPrefs = store.data

@FunBinding
suspend fun updateTwilightMode(
    store: TwilightPrefsStore,
    twilightMode: @Assisted TwilightMode
) {
    store.updateData { it.copy(twilightMode = twilightMode) }
}

@FunBinding
suspend fun updateUseBlackInDarkMode(
    store: TwilightPrefsStore,
    useBlackInDarkMode: @Assisted Boolean
) {
    store.updateData { it.copy(useBlackInDarkMode = useBlackInDarkMode) }
}

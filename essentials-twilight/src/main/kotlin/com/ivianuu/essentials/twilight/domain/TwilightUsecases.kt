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

package com.ivianuu.essentials.twilight.domain

import com.ivianuu.essentials.twilight.data.TwilightMode
import com.ivianuu.essentials.twilight.data.TwilightPrefs
import com.ivianuu.essentials.twilight.data.TwilightPrefsStore
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.Flow

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

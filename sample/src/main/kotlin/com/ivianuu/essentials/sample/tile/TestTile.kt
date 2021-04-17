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

package com.ivianuu.essentials.sample.tile

import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.tile.*
import com.ivianuu.essentials.twilight.data.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

@Given
fun testTileModel(
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given twilightPref: DataStore<TwilightPrefs>
): @Scoped<TileGivenScope> StateFlow<TileModel<FunTileService1>> = scope.state(TileModel()) {
    twilightPref.data.update { it.toTileModel() }
    action(TileModel.onTileClicked()) {
        val newTwilightMode = if (twilightPref.data.first().twilightMode == TwilightMode.LIGHT)
            TwilightMode.DARK else TwilightMode.LIGHT
        twilightPref.updateData { copy(twilightMode = newTwilightMode) }
    }
}

private fun TwilightPrefs.toTileModel() = TileModel<FunTileService1>(
    label = twilightMode.name,
    status = if (twilightMode == TwilightMode.LIGHT) TileModel.Status.ACTIVE
    else TileModel.Status.INACTIVE
)

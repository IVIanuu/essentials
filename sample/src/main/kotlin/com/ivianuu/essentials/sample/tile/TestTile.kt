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

import com.ivianuu.essentials.coroutines.collectIn
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.actions
import com.ivianuu.essentials.store.updateIn
import com.ivianuu.essentials.tile.FunTileService1
import com.ivianuu.essentials.tile.TileAction
import com.ivianuu.essentials.tile.TileAction.TileClicked
import com.ivianuu.essentials.tile.TileGivenScope
import com.ivianuu.essentials.tile.TileState
import com.ivianuu.essentials.twilight.data.TwilightMode
import com.ivianuu.essentials.twilight.data.TwilightPrefs
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.first

@Given
fun testTile(
    @Given twilightPref: DataStore<TwilightPrefs>
): StoreBuilder<TileGivenScope, TileState<FunTileService1>, TileAction<FunTileService1>> = {
    twilightPref.data.updateIn(this) { it.toTileState() }
    actions<TileClicked>().collectIn(this) {
        val newTwilightMode = if (twilightPref.data.first().twilightMode == TwilightMode.LIGHT)
            TwilightMode.DARK else TwilightMode.LIGHT
        twilightPref.updateData { copy(twilightMode = newTwilightMode) }
    }
}

private fun TwilightPrefs.toTileState() = TileState<FunTileService1>(
    label = twilightMode.name,
    status = if (twilightMode == TwilightMode.LIGHT) TileState.Status.ACTIVE
    else TileState.Status.INACTIVE
)

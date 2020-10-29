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

import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.store.setStateIn
import com.ivianuu.essentials.store.storeProvider
import com.ivianuu.essentials.tile.functional.TileState
import com.ivianuu.essentials.tile.functional.TileStore
import com.ivianuu.essentials.tile.functional.TileStoreBinding
import com.ivianuu.essentials.tile.functional.onEachTileClick
import com.ivianuu.essentials.tile.functional.tileStore
import com.ivianuu.essentials.twilight.TwilightMode
import com.ivianuu.essentials.twilight.twilightPrefs
import com.ivianuu.essentials.twilight.updateTwilightMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@TileStoreBinding
fun testTileStore(
    twilightPrefs: twilightPrefs,
    updateTwilightMode: updateTwilightMode
) = tileStore(
    index = 0,
    initial = TileState(
        iconRes = R.drawable.es_ic_accessibility,
        label = "Hello"
    )
) {
    twilightPrefs
        .map { it.twilightMode }
        .map {
            TileState(
                label = it.name,
                status = if (it == TwilightMode.Light) TileState.Status.Active
                else TileState.Status.Inactive
            )
        }
        .setStateIn(this) { it }

    onEachTileClick {
        updateTwilightMode(
            if (twilightPrefs.first().twilightMode == TwilightMode.Light) TwilightMode.Dark
            else TwilightMode.Light
        )
    }
}

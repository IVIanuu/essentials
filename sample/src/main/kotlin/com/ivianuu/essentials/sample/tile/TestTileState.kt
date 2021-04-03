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

import com.ivianuu.essentials.android.prefs.PrefAction
import com.ivianuu.essentials.android.prefs.update
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.tile.FunTileService1
import com.ivianuu.essentials.tile.TileAction
import com.ivianuu.essentials.tile.TileGivenScope
import com.ivianuu.essentials.tile.TileState
import com.ivianuu.essentials.twilight.data.TwilightMode
import com.ivianuu.essentials.twilight.data.TwilightPrefs
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@Given
fun testTileState(
    @Given scope: ScopeCoroutineScope<TileGivenScope>,
    @Given actions: Flow<TileAction>,
    @Given twilightPrefs: Flow<TwilightPrefs>,
    @Given twilightPrefUpdater: Collector<PrefAction<TwilightPrefs>>,
) = scope.state(TwilightPrefs().toTileState()) {
    twilightPrefs
        .update { it.toTileState() }
        .launchIn(this)
    actions
        .filterIsInstance<TileAction>()
        .map {
            if (twilightPrefs.first().twilightMode == TwilightMode.LIGHT) TwilightMode.DARK
            else TwilightMode.LIGHT
        }
        .onEach { twilightMode ->
            twilightPrefUpdater.update {
                copy(twilightMode = twilightMode)
            }
        }
        .launchIn(this)
}

private fun TwilightPrefs.toTileState() = TileState<FunTileService1>(
    label = twilightMode.name,
    status = if (twilightMode == TwilightMode.LIGHT) TileState.Status.ACTIVE
    else TileState.Status.INACTIVE
)

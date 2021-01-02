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

import com.ivianuu.essentials.datastore.android.updatePref
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.tile.FunTileService1
import com.ivianuu.essentials.tile.TileAction
import com.ivianuu.essentials.tile.TileStateBinding
import com.ivianuu.essentials.tile.TileState
import com.ivianuu.essentials.twilight.data.TwilightMode
import com.ivianuu.essentials.twilight.data.TwilightPrefs
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@TileStateBinding<FunTileService1> @Given
fun testTileState(
    @Given scope: CoroutineScope,
    @Given actions: Actions<TileAction>,
    @Given update: updatePref<TwilightPrefs>,
    @Given twilightPrefs: Flow<TwilightPrefs>,
) = scope.state(TwilightPrefs().toTileState()) {
    twilightPrefs
        .reduce { it.toTileState() }
        .launchIn(this)
    actions
        .filterIsInstance<TileAction>()
        .map {
            if (twilightPrefs.first().twilightMode == TwilightMode.Light) TwilightMode.Dark
            else TwilightMode.Light
        }
        .onEach { twilightMode ->
            update {
                copy(twilightMode = twilightMode)
            }
        }
        .launchIn(this)
}

private fun TwilightPrefs.toTileState() = TileState(
    label = twilightMode.name,
    status = if (twilightMode == TwilightMode.Light) TileState.Status.Active
    else TileState.Status.Inactive
)

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

import com.ivianuu.essentials.coroutines.par
import com.ivianuu.essentials.data.ValueAction
import com.ivianuu.essentials.data.update
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.actionsOf
import com.ivianuu.essentials.store.collectIn
import com.ivianuu.essentials.store.updateIn
import com.ivianuu.essentials.tile.FunTileService1
import com.ivianuu.essentials.tile.TileAction
import com.ivianuu.essentials.tile.TileAction.*
import com.ivianuu.essentials.tile.TileGivenScope
import com.ivianuu.essentials.tile.TileState
import com.ivianuu.essentials.tuples.Tuple2
import com.ivianuu.essentials.tuples.tupleOf
import com.ivianuu.essentials.twilight.data.TwilightMode
import com.ivianuu.essentials.twilight.data.TwilightPrefs
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private object NULL

@Suppress("UNCHECKED_CAST")
fun <A, B> Flow<A>.withLatestFrom(other: Flow<B>): Flow<Tuple2<A, B>> = channelFlow {
    val otherState = MutableStateFlow<Any?>(NULL)
    par(
        {
            other.collect(otherState)
        },
        {
            this@withLatestFrom
                .collect { a ->
                    val b = otherState.first { it !== NULL } as B
                    offer(tupleOf(a, b))
                }
        }
    )
}

@Given
fun testTile(
    @Given twilightPrefStore: Store<TwilightPrefs, ValueAction<TwilightPrefs>>
): StoreBuilder<TileGivenScope, TileState<FunTileService1>, TileAction> = {
    twilightPrefStore
        .updateIn(this) { it.toTileState() }

    actionsOf<TileClicked>()
        .withLatestFrom(twilightPrefStore)
        .map { (_, state) ->
            if (state.twilightMode == TwilightMode.LIGHT)
                TwilightMode.DARK else TwilightMode.LIGHT
        }
        .collectIn(this) {
            twilightPrefStore.update { copy(twilightMode = it) }
        }
}

private fun TwilightPrefs.toTileState() = TileState<FunTileService1>(
    label = twilightMode.name,
    status = if (twilightMode == TwilightMode.LIGHT) TileState.Status.ACTIVE
    else TileState.Status.INACTIVE
)

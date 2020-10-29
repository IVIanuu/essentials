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

package com.ivianuu.essentials.tile

import android.graphics.drawable.Icon
import com.ivianuu.essentials.store.StoreScope
import com.ivianuu.essentials.store.onEachAction
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.BindingAdapter
import com.ivianuu.injekt.MapEntries

typealias TileStore = suspend StoreScope<TileState, TileAction>.() -> Unit

fun tileStore(
    index: Int,
    initial: TileState,
    block: suspend StoreScope<TileState, TileAction>.() -> Unit
): TileStoreEntry = TileStoreEntry(index, initial, block)

data class TileStoreEntry(
    val index: Int,
    val initialState: TileState,
    val block: TileStore
)

typealias TileStores = Map<Int, TileStoreEntry>

data class TileState(
    val icon: Icon? = null,
    val iconRes: Int? = null,
    val label: String? = null,
    val labelRes: Int? = null,
    val description: String? = null,
    val descriptionRes: Int? = null,
    val status: Status = Status.Active
) {
    enum class Status {
        Unavailable, Active, Inactive
    }
}

fun Boolean.toTileStatus() = if (this) TileState.Status.Active else TileState.Status.Inactive

sealed class TileAction {
    object TileClicked : TileAction()
}

suspend fun StoreScope<TileState, TileAction>.onEachTileClick(block: suspend () -> Unit) {
    onEachAction {
        when (it) {
            TileAction.TileClicked -> block()
        }.exhaustive
    }
}

@BindingAdapter
annotation class TileStoreBinding {
    companion object {
        @MapEntries
        fun <T : TileStoreEntry> intoTileMap(instance: T): TileStores = mapOf(instance.index to instance)
    }
}

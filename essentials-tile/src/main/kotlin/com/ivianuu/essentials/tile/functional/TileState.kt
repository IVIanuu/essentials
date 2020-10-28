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

package com.ivianuu.essentials.tile.functional

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.store.StoreScope
import com.ivianuu.essentials.store.onEachAction
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.BindingAdapter
import com.ivianuu.injekt.MapEntries
import kotlinx.coroutines.CoroutineScope

typealias TileStore = (CoroutineScope) -> Store<TileState, TileAction>

typealias TileStores = Map<Int, () -> TileStore>

data class TileState(
    val icon: Icon? = null,
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
annotation class TileStore1Binding {
    companion object {
        @MapEntries
        fun <T : TileStore> intoTileMap(instance: () -> T): TileStores = mapOf(1 to instance)
    }
}

@BindingAdapter
annotation class TileStore2Binding {
    companion object {
        @MapEntries
        fun <T : TileStore> intoTileMap(instance: () -> T): TileStores = mapOf(2 to instance)
    }
}

@BindingAdapter
annotation class TileStore3Binding {
    companion object {
        @MapEntries
        fun <T : TileStore> intoTileMap(instance: () -> T): TileStores = mapOf(3 to instance)
    }
}

@BindingAdapter
annotation class TileStore4Binding {
    companion object {
        @MapEntries
        fun <T : TileStore> intoTileMap(instance: () -> T): TileStores = mapOf(4 to instance)
    }
}

@BindingAdapter
annotation class TileStore5Binding {
    companion object {
        @MapEntries
        fun <T : TileStore> intoTileMap(instance: () -> T): TileStores = mapOf(5 to instance)
    }
}

@BindingAdapter
annotation class TileStore6Binding {
    companion object {
        @MapEntries
        fun <T : TileStore> intoTileMap(instance: () -> T): TileStores = mapOf(6 to instance)
    }
}

@BindingAdapter
annotation class TileStore7Binding {
    companion object {
        @MapEntries
        fun <T : TileStore> intoTileMap(instance: () -> T): TileStores = mapOf(7 to instance)
    }
}

@BindingAdapter
annotation class TileStore8Binding {
    companion object {
        @MapEntries
        fun <T : TileStore> intoTileMap(instance: () -> T): TileStores = mapOf(8 to instance)
    }
}

@BindingAdapter
annotation class TileStore9Binding {
    companion object {
        @MapEntries
        fun <T : TileStore> intoTileMap(instance: () -> T): TileStores = mapOf(9 to instance)
    }
}

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

import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.tile.TileAction.*
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TileStoreService1 : AbstractTileStoreService()
class TileStoreService2 : AbstractTileStoreService()
class TileStoreService3 : AbstractTileStoreService()
class TileStoreService4 : AbstractTileStoreService()
class TileStoreService5 : AbstractTileStoreService()
class TileStoreService6 : AbstractTileStoreService()
class TileStoreService7 : AbstractTileStoreService()
class TileStoreService8 : AbstractTileStoreService()
class TileStoreService9 : AbstractTileStoreService()

abstract class AbstractTileStoreService : EsTileService() {

    private val component by lazy {
        serviceComponent.mergeComponent<FunTileServiceComponent>()
    }

    private val clicks = EventFlow<Unit>()

    override fun onStartListening() {
        super.onStartListening()
        listeningScope.launch {
            val tileId = packageManager.getServiceInfo(
                ComponentName(
                    this@AbstractTileStoreService,
                    this@AbstractTileStoreService.javaClass
                ),
                PackageManager.GET_META_DATA
            ).metaData?.getString(TILE_ID_KEY) ?: error("No id specified for $this")
            val (initialState, block) = (component.tileStores[tileId]?.invoke()
                ?: error("No tile found for $tileId"))
            val store = store(initialState, block)
            launch { clicks.collect { store.dispatch(TileClicked) } }
            launch { store.state.collect { applyState(it) } }
        }
    }

    override fun onClick() {
        super.onClick()
        clicks.emit(Unit)
    }

    private fun applyState(state: TileState) {
        val qsTile = qsTile ?: return

        qsTile.state = when (state.status) {
            TileState.Status.Active -> android.service.quicksettings.Tile.STATE_ACTIVE
            TileState.Status.Inactive -> android.service.quicksettings.Tile.STATE_INACTIVE
            TileState.Status.Unavailable -> android.service.quicksettings.Tile.STATE_UNAVAILABLE
        }
        qsTile.icon = when {
            state.icon != null -> state.icon
            state.iconRes != null -> Icon.createWithResource(this, state.iconRes)
            else -> null
        }
        qsTile.label = when {
            state.label != null -> state.label
            state.labelRes != null -> component.stringResource(state.labelRes)
            else -> null
        }
        qsTile.contentDescription = when {
            state.description != null -> state.description
            state.descriptionRes != null -> component.stringResource(state.descriptionRes)
            else -> null
        }
        qsTile.updateTile()
    }
}

const val TILE_ID_KEY = "com.ivianuu.essentials.tile.TILE_ID"

@MergeInto(ServiceComponent::class)
interface FunTileServiceComponent {
    val stringResource: stringResource
    val tileStores: TileStores
}

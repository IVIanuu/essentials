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

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.tile.EsTileService
import com.ivianuu.essentials.tile.functional.TileAction.*
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TileStoreService1 : AbstractTileStoreService(1)
class TileStoreService2 : AbstractTileStoreService(2)
class TileStoreService3 : AbstractTileStoreService(3)
class TileStoreService4 : AbstractTileStoreService(4)
class TileStoreService5 : AbstractTileStoreService(5)
class TileStoreService6 : AbstractTileStoreService(6)
class TileStoreService7 : AbstractTileStoreService(7)
class TileStoreService8 : AbstractTileStoreService(8)
class TileStoreService9 : AbstractTileStoreService(9)

abstract class AbstractTileStoreService(val index: Int) : EsTileService() {

    private val component by lazy {
        serviceComponent.mergeComponent<FunTileServiceComponent>()
    }

    private val clicks = EventFlow<Unit>()

    override fun onStartListening() {
        super.onStartListening()
        listeningScope.launch {
            val tileStore = (component.tileStores[index]
                ?: error("No tile found for $index"))()(this)
            launch { clicks.collect { tileStore.dispatch(TileClicked) } }
            launch { tileStore.state.collect { applyState(it) } }
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
        qsTile.icon = state.icon
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

@MergeInto(ServiceComponent::class)
interface FunTileServiceComponent {
    val stringResource: stringResource
    val tileStores: TileStores
}

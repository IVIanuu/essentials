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
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.tile.TileAction.TileClicked
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.component.ComponentElementBinding
import com.ivianuu.injekt.component.get
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FunTileService1 : AbstractFunTileService(typeKeyOf<FunTileService1>())
class FunTileService2 : AbstractFunTileService(typeKeyOf<FunTileService2>())
class FunTileService3 : AbstractFunTileService(typeKeyOf<FunTileService3>())
class FunTileService4 : AbstractFunTileService(typeKeyOf<FunTileService4>())
class FunTileService5 : AbstractFunTileService(typeKeyOf<FunTileService5>())
class FunTileService6 : AbstractFunTileService(typeKeyOf<FunTileService6>())
class FunTileService7 : AbstractFunTileService(typeKeyOf<FunTileService7>())
class FunTileService8 : AbstractFunTileService(typeKeyOf<FunTileService8>())
class FunTileService9 : AbstractFunTileService(typeKeyOf<FunTileService9>())

abstract class AbstractFunTileService(
    private val key: TypeKey<AbstractFunTileService>
) : EsTileService() {

    private val component by lazy {
        serviceComponent.get<FunTileServiceComponent>()
    }

    private val tileActions = EventFlow<TileAction>()

    override fun onStartListening() {
        super.onStartListening()
        listeningScope.launch {
            val store = (component.tileStores[key]
                ?.invoke(this, tileActions)
                ?: error("No tile found for $key"))
            store.collect { applyState(it) }
        }
    }

    override fun onClick() {
        super.onClick()
        tileActions.emit(TileClicked)
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
            state.labelRes != null -> component.resourceProvider.string(state.labelRes)
            else -> null
        }
        qsTile.contentDescription = when {
            state.description != null -> state.description
            state.descriptionRes != null -> component.resourceProvider.string(state.descriptionRes)
            else -> null
        }
        qsTile.updateTile()
    }
}

@ComponentElementBinding<ServiceComponent>
@Given
class FunTileServiceComponent(
    @Given val resourceProvider: ResourceProvider,
    @Given tileStores: Set<TileStateElement>
) {
    val tileStores = tileStores.toMap()
}

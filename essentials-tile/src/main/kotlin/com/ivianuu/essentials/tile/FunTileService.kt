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
import android.service.quicksettings.TileService
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.util.StringResourceProvider
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ServiceGivenScope
import com.ivianuu.injekt.android.createServiceGivenScope
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.scope.ChildScopeFactory
import com.ivianuu.injekt.scope.InstallElement
import com.ivianuu.injekt.scope.element
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FunTileService1 : AbstractFunTileService(typeKeyOf<FunTileService1>())
class FunTileService2 : AbstractFunTileService(typeKeyOf<FunTileService2>())
class FunTileService3 : AbstractFunTileService(typeKeyOf<FunTileService3>())
class FunTileService4 : AbstractFunTileService(typeKeyOf<FunTileService4>())
class FunTileService5 : AbstractFunTileService(typeKeyOf<FunTileService5>())
class FunTileService6 : AbstractFunTileService(typeKeyOf<FunTileService6>())
class FunTileService7 : AbstractFunTileService(typeKeyOf<FunTileService7>())
class FunTileService8 : AbstractFunTileService(typeKeyOf<FunTileService8>())
class FunTileService9 : AbstractFunTileService(typeKeyOf<FunTileService9>())

abstract class AbstractFunTileService(private val tileKey: TypeKey<AbstractFunTileService>) : TileService() {

    private val component by lazy {
        createServiceGivenScope()
            .element<FunTileServiceComponent>()
    }

    private var tileStateComponent: TileStateComponent? = null

    override fun onStartListening() {
        super.onStartListening()
        component.logger.d { "$tileKey on start listening" }
        val tileStateComponent = component.tileGivenScopeFactory(tileKey)
            .element<TileStateComponent>()
            .also { this.tileStateComponent = it }
        tileStateComponent.tileState
            .onEach { applyState(it) }
            .launchIn(tileStateComponent.scope)
    }

    override fun onClick() {
        super.onClick()
        component.logger.d { "$tileKey on click" }
        tileStateComponent!!.tileState.value.onTileClicked()
    }

    override fun onStopListening() {
        tileStateComponent?.tileGivenScope?.dispose()
        tileStateComponent = null
        component.logger.d { "$tileKey on stop listening" }
        super.onStopListening()
    }

    override fun onDestroy() {
        component.serviceGivenScope.dispose()
        super.onDestroy()
    }

    private fun applyState(state: TileState<*>) {
        val qsTile = qsTile ?: return

        qsTile.state = when (state.status) {
            TileState.Status.ACTIVE -> android.service.quicksettings.Tile.STATE_ACTIVE
            TileState.Status.INACTIVE -> android.service.quicksettings.Tile.STATE_INACTIVE
            TileState.Status.UNAVAILABLE -> android.service.quicksettings.Tile.STATE_UNAVAILABLE
        }
        qsTile.icon = when {
            state.icon != null -> state.icon
            state.iconRes != null -> Icon.createWithResource(this, state.iconRes)
            else -> null
        }
        qsTile.label = when {
            state.label != null -> state.label
            state.labelRes != null -> component.stringResource(state.labelRes, emptyList())
            else -> null
        }
        qsTile.contentDescription = when {
            state.description != null -> state.description
            state.descriptionRes != null -> component.stringResource(state.descriptionRes, emptyList())
            else -> null
        }
        qsTile.updateTile()
    }
}

@InstallElement<ServiceGivenScope>
@Given
class FunTileServiceComponent(
    @Given val logger: Logger,
    @Given val serviceGivenScope: ServiceGivenScope,
    @Given val stringResource: StringResourceProvider,
    @Given val tileGivenScopeFactory: @ChildScopeFactory (TypeKey<AbstractFunTileService>) -> TileGivenScope
)

@InstallElement<TileGivenScope>
@Given
class TileStateComponent(
    @Given tileKey: TypeKey<AbstractFunTileService>,
    @Given tileStateElements: Map<TypeKey<AbstractFunTileService>, () -> StateFlow<TileState<*>>> = emptyMap(),
    @Given val scope: ScopeCoroutineScope<TileGivenScope>,
    @Given val tileGivenScope: TileGivenScope
) {
    val tileState = tileStateElements[tileKey]
        ?.invoke()
        ?: error("No tile found for $tileKey in ${tileStateElements.toMap()}")
}

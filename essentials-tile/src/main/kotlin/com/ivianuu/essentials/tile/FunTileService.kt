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

import android.graphics.drawable.*
import android.service.quicksettings.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

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

    private var tileModelComponent: TileModelComponent? = null

    override fun onStartListening() {
        super.onStartListening()
        component.logger.d { "$tileKey on start listening" }
        val tileModelComponent = component.tileGivenScopeFactory(tileKey)
            .element<TileModelComponent>()
            .also { this.tileModelComponent = it }
        tileModelComponent.tileModel
            .onEach { applyModel(it) }
            .launchIn(tileModelComponent.scope)
    }

    override fun onClick() {
        super.onClick()
        component.logger.d { "$tileKey on click" }
        tileModelComponent!!.tileModel.value.onTileClicked()
    }

    override fun onStopListening() {
        tileModelComponent?.tileGivenScope?.dispose()
        tileModelComponent = null
        component.logger.d { "$tileKey on stop listening" }
        super.onStopListening()
    }

    override fun onDestroy() {
        component.serviceGivenScope.dispose()
        super.onDestroy()
    }

    private fun applyModel(model: TileModel<*>) {
        val qsTile = qsTile ?: return

        qsTile.state = when (model.status) {
            TileModel.Status.ACTIVE -> Tile.STATE_ACTIVE
            TileModel.Status.INACTIVE -> Tile.STATE_INACTIVE
            TileModel.Status.UNAVAILABLE -> Tile.STATE_UNAVAILABLE
        }
        qsTile.icon = when {
            model.icon != null -> model.icon
            model.iconRes != null -> Icon.createWithResource(this, model.iconRes)
            else -> null
        }
        qsTile.label = when {
            model.label != null -> model.label
            model.labelRes != null -> component.stringResource(model.labelRes, emptyList())
            else -> null
        }
        qsTile.contentDescription = when {
            model.description != null -> model.description
            model.descriptionRes != null -> component.stringResource(model.descriptionRes, emptyList())
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
class TileModelComponent(
    @Given tileKey: TypeKey<AbstractFunTileService>,
    @Given tileModelElements: Set<Pair<TypeKey<AbstractFunTileService>, () -> StateFlow<TileModel<*>>>> = emptySet(),
    @Given val scope: GivenCoroutineScope<TileGivenScope>,
    @Given val tileGivenScope: TileGivenScope
) {
    val tileModel = tileModelElements.toMap()[tileKey]
        ?.invoke()
        ?: error("No tile found for $tileKey in ${tileModelElements.toMap()}")
}

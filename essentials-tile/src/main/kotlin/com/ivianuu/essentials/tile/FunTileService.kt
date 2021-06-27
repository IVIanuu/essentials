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
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

class FunTileService1 : AbstractFunTileService<FunTileService1>()
class FunTileService2 : AbstractFunTileService<FunTileService2>()
class FunTileService3 : AbstractFunTileService<FunTileService3>()
class FunTileService4 : AbstractFunTileService<FunTileService4>()
class FunTileService5 : AbstractFunTileService<FunTileService5>()
class FunTileService6 : AbstractFunTileService<FunTileService6>()
class FunTileService7 : AbstractFunTileService<FunTileService7>()
class FunTileService8 : AbstractFunTileService<FunTileService8>()
class FunTileService9 : AbstractFunTileService<FunTileService9>()

abstract class AbstractFunTileService<T : Any>(
  @Inject private val serviceClass: KClass<T>
) : TileService() {
  private val component: FunTileServiceComponent by lazy {
    createServiceScope().element()
  }

  @Provide private val logger
    get() = component.logger

  private var tileModelComponent: TileModelComponent? = null

  override fun onStartListening() {
    super.onStartListening()
    d { "$serviceClass on start listening" }
    val tileModelComponent = component.tileScopeFactory(TileId(serviceClass))
      .element<TileModelComponent>()
      .also { this.tileModelComponent = it }
    tileModelComponent.tileModel
      .onEach { applyModel(it) }
      .launchIn(tileModelComponent.scope)
  }

  override fun onClick() {
    super.onClick()
    d { "$serviceClass on click" }
    tileModelComponent!!.tileModel.value.onTileClicked()
  }

  override fun onStopListening() {
    tileModelComponent?.tileScope?.cast<DisposableScope>()?.dispose()
    tileModelComponent = null
    d { "$serviceClass on stop listening" }
    super.onStopListening()
  }

  override fun onDestroy() {
    component.serviceScope.cast<DisposableScope>().dispose()
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
      model.labelRes != null -> component.resourceProvider<String>(model.labelRes)
      else -> null
    }
    qsTile.contentDescription = when {
      model.description != null -> model.description
      model.descriptionRes != null -> component.resourceProvider<String>(model.descriptionRes)
      else -> null
    }
    qsTile.updateTile()
  }
}

@Provide @ScopeElement<ServiceScope>
class FunTileServiceComponent(
  val logger: Logger,
  val resourceProvider: ResourceProvider,
  val serviceScope: ServiceScope,
  val tileScopeFactory: @ChildScopeFactory (TileId) -> TileScope
)

@Provide @ScopeElement<TileScope>
class TileModelComponent(
  tileId: TileId,
  tileModelElements: Set<Pair<TileId, () -> StateFlow<TileModel<*>>>> = emptySet(),
  val scope: InjektCoroutineScope<TileScope>,
  val tileScope: TileScope
) {
  val tileModel = tileModelElements.toMap()[tileId]
    ?.invoke()
    ?: error("No tile found for $tileId in ${tileModelElements.toMap()}")
}

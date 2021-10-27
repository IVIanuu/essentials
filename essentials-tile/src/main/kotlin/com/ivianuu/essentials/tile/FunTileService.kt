/*
 * Copyright 2021 Manuel Wrage
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
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.android.createServiceComponent
import com.ivianuu.injekt.common.EntryPoint
import com.ivianuu.injekt.common.dispose
import com.ivianuu.injekt.common.entryPoint
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

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
    createServiceComponent().entryPoint()
  }

  @Provide private val logger
    get() = component.logger

  private var tileModelHolder: TileModelHolder? = null

  override fun onStartListening() {
    super.onStartListening()
    log { "$serviceClass on start listening" }
    val tileModelHolder = component.tileComponentFactory
      .tileComponent(TileId(serviceClass))
      .entryPoint<TileModelComponent>()
      .holder
      .also { this.tileModelHolder = it }
    tileModelHolder.tileModel
      .onEach { applyModel(it) }
      .launchIn(tileModelHolder.scope)
  }

  override fun onClick() {
    super.onClick()
    log { "$serviceClass on click" }
    tileModelHolder?.tileModel?.value?.onTileClicked?.invoke()
  }

  override fun onStopListening() {
    tileModelHolder?.component?.dispose()
    tileModelHolder = null
    log { "$serviceClass on stop listening" }
    super.onStopListening()
  }

  override fun onDestroy() {
    component.dispose()
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

@EntryPoint<ServiceComponent> interface FunTileServiceComponent {
  val logger: Logger
  val resourceProvider: ResourceProvider
  val tileComponentFactory: TileComponentFactory
}

@Provide class TileModelHolder(
  tileId: TileId,
  tileModelElements: List<Pair<TileId, () -> StateFlow<TileModel<*>>>> = emptyList(),
  val scope: ComponentScope<TileComponent>,
  val component: TileComponent
) {
  val tileModel = tileModelElements.toMap()[tileId]
    ?.invoke()
    ?: error("No tile found for $tileId in ${tileModelElements.toMap()}")
}

@EntryPoint<TileComponent> interface TileModelComponent {
  val holder: TileModelHolder
}

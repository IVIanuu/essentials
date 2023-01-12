/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tile

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ivianuu.essentials.AppElementsOwner
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.state.state
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element
import com.ivianuu.injekt.common.Scope
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
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
  private val serviceComponent by lazy {
    application
      .cast<AppElementsOwner>()
      .appElements
      .element<FunTileServiceComponent>()
  }

  @Provide private val logger
    get() = serviceComponent.logger

  private var tileComponent: TileModelComponent? = null

  override fun onStartListening() {
    super.onStartListening()
    log { "$serviceClass on start listening" }
    val tileModelComponent = serviceComponent.tileModelComponent(Scope(), TileId(serviceClass))
      .also { this.tileComponent = it }
    tileModelComponent.tileModel
      .onEach { applyModel(it) }
      .launchIn(tileModelComponent.coroutineScope)
  }

  override fun onClick() {
    super.onClick()
    log { "$serviceClass on click" }
    tileComponent?.currentModel?.onTileClicked?.invoke()
  }

  override fun onStopListening() {
    tileComponent?.scope?.dispose()
    tileComponent = null
    log { "$serviceClass on stop listening" }
    super.onStopListening()
  }

  private fun applyModel(model: TileModel<*>) {
    tileComponent?.currentModel = model
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
    with(serviceComponent.resourceProvider) {
      qsTile.label = when {
        model.label != null -> model.label
        model.labelRes != null -> loadResource<String>(model.labelRes)
        else -> null
      }
      qsTile.contentDescription = when {
        model.description != null -> model.description
        model.descriptionRes != null -> loadResource<String>(model.descriptionRes)
        else -> null
      }
    }
    qsTile.updateTile()
  }
}

@Provide @Element<AppScope> data class FunTileServiceComponent(
  val logger: Logger,
  val resourceProvider: ResourceProvider,
  val tileModelComponent: (Scope<TileScope>, TileId) -> TileModelComponent
)

@Provide @Element<TileScope> data class TileModelComponent(
  val tileId: TileId,
  val tileModelElements: List<Pair<TileId, Model<TileModel<*>>>>,
  val coroutineScope: NamedCoroutineScope<TileScope>,
  val scope: Scope<TileScope>
) {
  var currentModel: TileModel<*>? = null

  val tileModel = state {
    tileModelElements.toMap()[tileId]
      ?.invoke()
      ?: error("No tile found for $tileId in ${tileModelElements.toMap()}")
  }
}

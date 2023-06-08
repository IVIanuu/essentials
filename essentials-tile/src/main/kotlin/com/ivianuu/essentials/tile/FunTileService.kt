/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tile

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.compose.state
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element
import com.ivianuu.injekt.common.NamedCoroutineScope
import com.ivianuu.injekt.common.Scope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

@Provide @AndroidComponent class FunTileService1(
  logger: Logger,
  resources: Resources,
  tileModelComponent: (Scope<TileScope>, TileId) -> TileModelComponent
) : AbstractFunTileService<FunTileService1>()

@Provide @AndroidComponent class FunTileService2(
  logger: Logger,
  resources: Resources,
  tileModelComponent: (Scope<TileScope>, TileId) -> TileModelComponent
) : AbstractFunTileService<FunTileService2>()

@Provide @AndroidComponent class FunTileService3(
  logger: Logger,
  resources: Resources,
  tileModelComponent: (Scope<TileScope>, TileId) -> TileModelComponent
) : AbstractFunTileService<FunTileService3>()

@Provide @AndroidComponent class FunTileService4(
  logger: Logger,
  resources: Resources,
  tileModelComponent: (Scope<TileScope>, TileId) -> TileModelComponent
) : AbstractFunTileService<FunTileService4>()

@Provide @AndroidComponent class FunTileService5(
  logger: Logger,
  resources: Resources,
  tileModelComponent: (Scope<TileScope>, TileId) -> TileModelComponent
) : AbstractFunTileService<FunTileService5>()

@Provide @AndroidComponent class FunTileService6(
  logger: Logger,
  resources: Resources,
  tileModelComponent: (Scope<TileScope>, TileId) -> TileModelComponent
) : AbstractFunTileService<FunTileService6>()

@Provide @AndroidComponent class FunTileService7(
  logger: Logger,
  resources: Resources,
  tileModelComponent: (Scope<TileScope>, TileId) -> TileModelComponent
) : AbstractFunTileService<FunTileService7>()

@Provide @AndroidComponent class FunTileService8(
  logger: Logger,
  resources: Resources,
  tileModelComponent: (Scope<TileScope>, TileId) -> TileModelComponent
) : AbstractFunTileService<FunTileService8>()

@Provide @AndroidComponent class FunTileService9(
  logger: Logger,
  resources: Resources,
  tileModelComponent: (Scope<TileScope>, TileId) -> TileModelComponent
) : AbstractFunTileService<FunTileService9>()

abstract class AbstractFunTileService<T : Any>(
  @Inject private val logger: Logger,
  @Inject private val resources: Resources,
  @Inject private val serviceClass: KClass<T>,
  @Inject private val tileModelComponent: (Scope<TileScope>, TileId) -> TileModelComponent
) : TileService() {
  private var tileComponent: TileModelComponent? = null

  override fun onStartListening() {
    super.onStartListening()
    logger.log { "$serviceClass on start listening" }
    val tileModelComponent = tileModelComponent(Scope(), TileId(serviceClass))
      .also { this.tileComponent = it }
    tileModelComponent.tileModel
      .onEach { applyModel(it) }
      .launchIn(tileModelComponent.coroutineScope)
  }

  override fun onClick() {
    super.onClick()
    logger.log { "$serviceClass on click" }
    tileComponent?.currentModel?.onTileClicked?.invoke()
  }

  override fun onStopListening() {
    tileComponent?.scope?.dispose()
    tileComponent = null
    logger.log { "$serviceClass on stop listening" }
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
    qsTile.label = when {
      model.label != null -> model.label
      model.labelRes != null -> resources<String>(model.labelRes)
      else -> null
    }
    qsTile.contentDescription = when {
      model.description != null -> model.description
      model.descriptionRes != null -> resources<String>(model.descriptionRes)
      else -> null
    }
    qsTile.updateTile()
  }
}

@Provide @Element<TileScope> data class TileModelComponent(
  val tileId: TileId,
  val tileModelElements: List<Pair<TileId, Model<TileModel<*>>>>,
  val coroutineScope: NamedCoroutineScope<TileScope>,
  val scope: Scope<TileScope>
) {
  var currentModel: TileModel<*>? = null

  val tileModel = coroutineScope.state {
    tileModelElements.toMap()[tileId]
      ?.invoke()
      ?: error("No tile found for $tileId in ${tileModelElements.toMap()}")
  }
}

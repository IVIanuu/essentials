/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tile

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.Service
import com.ivianuu.essentials.compose.composedState
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

@Provide @AndroidComponent class FunTileService1(
  logger: Logger,
  resources: Resources,
  tileScopeFactory: (TileId) -> Scope<TileScope>
) : AbstractFunTileService<FunTileService1>()

@Provide @AndroidComponent class FunTileService2(
  logger: Logger,
  resources: Resources,
  tileScopeFactory: (TileId) -> Scope<TileScope>
) : AbstractFunTileService<FunTileService2>()

@Provide @AndroidComponent class FunTileService3(
  logger: Logger,
  resources: Resources,
  tileScopeFactory: (TileId) -> Scope<TileScope>
) : AbstractFunTileService<FunTileService3>()

@Provide @AndroidComponent class FunTileService4(
  logger: Logger,
  resources: Resources,
  tileScopeFactory: (TileId) -> Scope<TileScope>
) : AbstractFunTileService<FunTileService4>()

@Provide @AndroidComponent class FunTileService5(
  logger: Logger,
  resources: Resources,
  tileScopeFactory: (TileId) -> Scope<TileScope>
) : AbstractFunTileService<FunTileService5>()

@Provide @AndroidComponent class FunTileService6(
  logger: Logger,
  resources: Resources,
  tileScopeFactory: (TileId) -> Scope<TileScope>
) : AbstractFunTileService<FunTileService6>()

@Provide @AndroidComponent class FunTileService7(
  logger: Logger,
  resources: Resources,
  tileScopeFactory: (TileId) -> Scope<TileScope>
) : AbstractFunTileService<FunTileService7>()

@Provide @AndroidComponent class FunTileService8(
  logger: Logger,
  resources: Resources,
  tileScopeFactory: (TileId) -> Scope<TileScope>
) : AbstractFunTileService<FunTileService8>()

@Provide @AndroidComponent class FunTileService9(
  logger: Logger,
  resources: Resources,
  tileScopeFactory: (TileId) -> Scope<TileScope>
) : AbstractFunTileService<FunTileService9>()

abstract class AbstractFunTileService<T : Any>(
  @Inject private val logger: Logger,
  @Inject private val resources: Resources,
  @Inject private val serviceClass: KClass<T>,
  @Inject private val tileScopeFactory: (TileId) -> Scope<TileScope>
) : TileService() {
  private var tileScope: Scope<TileScope>? = null
  private var currentModel: TileModel<*>? = null

  override fun onStartListening() {
    super.onStartListening()
    logger.log { "$serviceClass on start listening" }
    tileScope = tileScopeFactory(TileId(serviceClass))
    val tileComponent = tileScope!!.service<TileComponent>()
    tileComponent
      .tileModel
      .onEach { applyModel(it) }
      .launchIn(tileComponent.coroutineScope)
  }

  override fun onClick() {
    super.onClick()
    logger.log { "$serviceClass on click" }
    currentModel?.onTileClicked?.invoke()
  }

  override fun onStopListening() {
    tileScope?.dispose()
    tileScope = null
    logger.log { "$serviceClass on stop listening" }
    super.onStopListening()
  }

  private fun applyModel(model: TileModel<*>) {
    currentModel = model
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

@Provide @Service<TileScope> data class TileComponent(
  val tileId: TileId,
  val tileModelElements: List<Pair<TileId, Model<TileModel<*>>>>,
  val coroutineScope: ScopedCoroutineScope<TileScope>,
  val scope: Scope<TileScope>
) {
  val tileModel = coroutineScope.composedState {
    tileModelElements.toMap()[tileId]
      ?.invoke()
      ?: error("No tile found for $tileId in ${tileModelElements.toMap()}")
  }
}

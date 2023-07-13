/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.Service
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.compose.compositionStateFlow
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.context
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

@Provide @AndroidComponent class EsTileService1(
  logger: Logger,
  tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService1>(logger, context(), tileScopeFactory)

@Provide @AndroidComponent class EsTileService2(
  logger: Logger,
  tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService2>(logger, context(), tileScopeFactory)

@Provide @AndroidComponent class EsTileService3(
  logger: Logger,
  tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService3>(logger, context(), tileScopeFactory)

@Provide @AndroidComponent class EsTileService4(
  logger: Logger,
  tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService4>(logger, context(), tileScopeFactory)

@Provide @AndroidComponent class EsTileService5(
  logger: Logger,
  tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService5>(logger, context(), tileScopeFactory)

@Provide @AndroidComponent class EsTileService6(
  logger: Logger,
  tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService6>(logger, context(), tileScopeFactory)

@Provide @AndroidComponent class EsTileService7(
  logger: Logger,
  tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService7>(logger, context(), tileScopeFactory)

@Provide @AndroidComponent class EsTileService8(
  logger: Logger,
  tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService8>(logger, context(), tileScopeFactory)

@Provide @AndroidComponent class EsTileService9(
  logger: Logger,
  tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService9>(logger, context(), tileScopeFactory)

abstract class AbstractEsTileService<T : AbstractEsTileService<T>>(
  @property:Provide private val logger: Logger,
  private val serviceClass: KClass<T>,
  private val tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : TileService() {
  private var tileScope: Scope<TileScope>? = null
  private var currentModel: TileModel<*>? = null

  override fun onStartListening() {
    super.onStartListening()
    log { "$serviceClass on start listening" }
    tileScope = tileScopeFactory(serviceClass.cast())
    val tileComponent = tileScope!!.service<TileComponent>()
    tileComponent
      .tileModel
      .onEach { applyModel(it) }
      .launchIn(tileComponent.coroutineScope)
  }

  override fun onClick() {
    super.onClick()
    log { "$serviceClass on click" }
    currentModel?.onClick?.invoke()
  }

  override fun onStopListening() {
    tileScope?.dispose()
    tileScope = null
    log { "$serviceClass on stop listening" }
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
    qsTile.icon = model.icon
    qsTile.label = model.label
    qsTile.contentDescription = model.description
    qsTile.updateTile()
  }
}

@Provide @Service<TileScope> data class TileComponent(
  val tileClass: KClass<AbstractEsTileService<*>>,
  val tileModelRecords: Map<KClass<AbstractEsTileService<*>>, () -> Model<TileModel<*>>>,
  val coroutineScope: ScopedCoroutineScope<TileScope>,
  val scope: Scope<TileScope>
) {
  private val model = tileModelRecords[tileClass]?.invoke()
    ?: error("No tile found for $tileClass in ${tileModelRecords.toMap()}")
  val tileModel = coroutineScope.compositionStateFlow { model() }
}

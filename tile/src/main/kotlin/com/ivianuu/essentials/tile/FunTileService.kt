/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.Service
import com.ivianuu.essentials.compose.compositionStateFlow
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

@Provide @AndroidComponent class EsTileService1(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService1>()

@Provide @AndroidComponent class EsTileService2(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService2>()

@Provide @AndroidComponent class EsTileService3(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService3>()

@Provide @AndroidComponent class EsTileService4(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService4>()

@Provide @AndroidComponent class EsTileService5(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService5>()

@Provide @AndroidComponent class EsTileService6(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService6>()

@Provide @AndroidComponent class EsTileService7(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService7>()

@Provide @AndroidComponent class EsTileService8(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService8>()

@Provide @AndroidComponent class EsTileService9(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (KClass<AbstractEsTileService<*>>) -> Scope<TileScope>
) : AbstractEsTileService<EsTileService9>()

abstract class AbstractEsTileService<T : Any>(
  @Inject private val logger: Logger,
  @Inject private val serviceClass: KClass<T>,
  @Inject private val tileScopeFactory: (KClass<*>) -> Scope<TileScope>
) : TileService() {
  private var tileScope: Scope<TileScope>? = null
  private var currentModel: TileModel<*>? = null

  override fun onStartListening() {
    super.onStartListening()
    logger.log { "$serviceClass on start listening" }
    tileScope = tileScopeFactory(serviceClass)
    val tileComponent = tileScope!!.service<TileComponent>()
    tileComponent
      .tileModel
      .onEach { applyModel(it) }
      .launchIn(tileComponent.coroutineScope)
  }

  override fun onClick() {
    super.onClick()
    logger.log { "$serviceClass on click" }
    currentModel?.onClick?.invoke()
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

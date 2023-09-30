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
import com.ivianuu.essentials.ui.navigation.Presenter
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

@Provide @AndroidComponent class EsTileService1(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService()

@Provide @AndroidComponent class EsTileService2(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService()

@Provide @AndroidComponent class EsTileService3(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService()

@Provide @AndroidComponent class EsTileService4(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService()

@Provide @AndroidComponent class EsTileService5(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService()

@Provide @AndroidComponent class EsTileService6(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService()

@Provide @AndroidComponent class EsTileService7(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService()

@Provide @AndroidComponent class EsTileService8(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService()

@Provide @AndroidComponent class EsTileService9(
  @Inject logger: Logger,
  @Inject tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService()

abstract class AbstractEsTileService(
  @Inject private val logger: Logger,
  @Inject private val tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : TileService() {
  private var tileScope: Scope<TileScope>? = null
  private var currentState: TileState<*>? = null

  override fun onStartListening() {
    super.onStartListening()
    logger.log { "${this::class} on start listening" }
    tileScope = tileScopeFactory(this)
    val tileComponent = tileScope!!.service<TileComponent>()
    tileComponent
      .tileState
      .onEach { applyState(it) }
      .launchIn(tileComponent.coroutineScope)
  }

  override fun onClick() {
    super.onClick()
    logger.log { "${this::class} on click" }
    currentState?.onClick?.invoke()
  }

  override fun onStopListening() {
    tileScope?.dispose()
    tileScope = null
    logger.log { "${this::class} on stop listening" }
    super.onStopListening()
  }

  private fun applyState(state: TileState<*>) {
    currentState = state
    val qsTile = qsTile ?: return

    qsTile.state = when (state.status) {
      TileState.Status.ACTIVE -> Tile.STATE_ACTIVE
      TileState.Status.INACTIVE -> Tile.STATE_INACTIVE
      TileState.Status.UNAVAILABLE -> Tile.STATE_UNAVAILABLE
    }
    qsTile.icon = state.icon
    qsTile.label = state.label
    qsTile.contentDescription = state.description
    qsTile.updateTile()
  }
}

@Provide @Service<TileScope> data class TileComponent(
  val tileService: AbstractEsTileService,
  val tilePresenterRecords: Map<KClass<AbstractEsTileService>, () -> Presenter<TileState<*>>>,
  val coroutineScope: ScopedCoroutineScope<TileScope>
) {
  private val presenter = tilePresenterRecords[tileService::class]?.invoke()
    ?: error("No tile found for ${tileService::class} in ${tilePresenterRecords.toMap()}")
  val tileState = coroutineScope.compositionStateFlow { presenter() }
}

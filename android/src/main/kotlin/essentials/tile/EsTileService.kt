/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.tile

import android.service.quicksettings.*
import androidx.compose.runtime.*
import essentials.*
import essentials.compose.*
import essentials.logging.*
import injekt.*
import kotlin.reflect.*

@Provide @AndroidComponent class EsTileService1(
  logger: Logger,
  tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService(logger, tileScopeFactory)

@Provide @AndroidComponent class EsTileService2(
  logger: Logger,
  tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService(logger, tileScopeFactory)

@Provide @AndroidComponent class EsTileService3(
  logger: Logger,
  tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService(logger, tileScopeFactory)

@Provide @AndroidComponent class EsTileService4(
  logger: Logger,
  tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService(logger, tileScopeFactory)

@Provide @AndroidComponent class EsTileService5(
  logger: Logger,
  tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : AbstractEsTileService(logger, tileScopeFactory)

@Stable abstract class AbstractEsTileService(
  private val logger: Logger,
  private val tileScopeFactory: (AbstractEsTileService) -> Scope<TileScope>
) : TileService() {
  private var tileScope: Scope<TileScope>? = null
  private var currentOnClick: (() -> Unit)? = null

  override fun onStartListening() {
    super.onStartListening()
    logger.d { "${this::class} on start listening" }
    tileScope = tileScopeFactory(this)
    tileScope!!.coroutineScope.launchMolecule {
      val presenter = remember {
        val tileComponent = tileScope!!.service<TileComponent>()
        tileComponent.tilePresenterRecords[this::class]?.invoke()
          ?: error("No presenter found for ${this::class} in ${tileComponent.tilePresenterRecords}")
      }

      val currentState = presenter.present()
        .also { this.currentOnClick = it.onClick }

      LaunchedEffect(currentState) {
        val qsTile = qsTile ?: return@LaunchedEffect
        qsTile.state = when (currentState.status) {
          TileState.Status.ACTIVE -> Tile.STATE_ACTIVE
          TileState.Status.INACTIVE -> Tile.STATE_INACTIVE
          TileState.Status.UNAVAILABLE -> Tile.STATE_UNAVAILABLE
        }
        qsTile.icon = currentState.icon
        qsTile.label = currentState.label
        qsTile.contentDescription = currentState.description
        qsTile.updateTile()
      }
    }
  }

  override fun onClick() {
    super.onClick()
    logger.d { "${this::class} on click" }
    currentOnClick?.invoke()
  }

  override fun onStopListening() {
    tileScope?.dispose()
    tileScope = null
    logger.d { "${this::class} on stop listening" }
    super.onStopListening()
  }
}

@Provide @Service<TileScope> data class TileComponent(
  val tilePresenterRecords: Map<KClass<out AbstractEsTileService>, () -> Presenter<TileState<*>>>
)

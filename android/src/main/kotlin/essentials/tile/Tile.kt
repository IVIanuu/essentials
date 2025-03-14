/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.tile

import android.graphics.drawable.*
import android.service.quicksettings.*
import androidx.compose.runtime.*
import essentials.*
import essentials.compose.*
import essentials.coroutines.coroutineScope
import essentials.logging.*
import injekt.*

abstract class EsTileService : TileService() {
  @Composable protected abstract fun state(): TileState

  private var currentOnClick: (() -> Unit)? = null

  @Provide private val appScope by lazy {
    applicationContext.cast<AppScopeOwner>()
      .appScope
  }
  private val component by lazy { service<TileServiceComponent>() }

  private var tileScope: Scope<TileScope>? = null

  override fun onStartListening() {
    super.onStartListening()
    d { "${this::class} on start listening" }
    tileScope = component.tileScopeFactory(this)
    coroutineScope(tileScope!!).launchMolecule {
      val currentState = state()
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
    d { "${this::class} on click" }
    currentOnClick?.invoke()
  }

  override fun onStopListening() {
    tileScope?.dispose()
    tileScope = null
    d { "${this::class} on stop listening" }
    super.onStopListening()
  }
}

@Provide @Service<AppScope> data class TileServiceComponent(
  val tileScopeFactory: (EsTileService) -> Scope<TileScope>
)

data class TileState(
  val label: String? = null,
  val icon: Icon? = null,
  val description: String? = null,
  val status: Status = Status.ACTIVE,
  val onClick: (() -> Unit)? = null
) {
  enum class Status { UNAVAILABLE, ACTIVE, INACTIVE }
}

data object TileScope

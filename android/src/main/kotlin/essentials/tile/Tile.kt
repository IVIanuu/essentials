/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.tile

import android.graphics.drawable.*
import essentials.*
import injekt.*
import kotlin.reflect.*

data class TileState<out T : AbstractEsTileService>(
  val label: String? = null,
  val icon: Icon? = null,
  val description: String? = null,
  val status: Status = Status.ACTIVE,
  val onClick: () -> Unit = {}
) {
  enum class Status { UNAVAILABLE, ACTIVE, INACTIVE }

  @Provide companion object {
    @Provide fun <@AddOn T : Presenter<out TileState<S>>, S : AbstractEsTileService> tilePresenterBinding(
      serviceClass: KClass<S>,
      presenter: () -> T
    ): Pair<KClass<out AbstractEsTileService>, () -> Presenter<TileState<*>>> = (serviceClass to presenter).cast()

    @Provide val defaultTilePresenterBindings
      get() = emptyList<Pair<KClass<out AbstractEsTileService>, () -> Presenter<TileState<*>>>>()
  }
}

data object TileScope

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tile

import android.graphics.drawable.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlin.reflect.*

data class TileState<out T : AbstractEsTileService>(
  val icon: Icon? = null,
  val label: String? = null,
  val description: String? = null,
  val status: Status = Status.ACTIVE,
  val onClick: () -> Unit = {}
) {
  enum class Status { UNAVAILABLE, ACTIVE, INACTIVE }

  @Provide companion object {
    @Provide fun <@Spread T : Presenter<TileState<S>>, S : AbstractEsTileService> tilePresenters(
      serviceClass: KClass<S>,
      presenter: () -> T
    ): Pair<KClass<AbstractEsTileService>, () -> Presenter<TileState<*>>> = (serviceClass to presenter).cast()

    @Provide val defaultTilePresenters get() = emptyList<Pair<KClass<AbstractEsTileService>, () -> Presenter<TileState<*>>>>()
  }
}

data object TileScope

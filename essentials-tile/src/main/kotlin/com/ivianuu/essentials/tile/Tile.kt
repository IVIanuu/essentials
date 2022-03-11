/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tile

import android.graphics.drawable.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlin.reflect.*

data class TileModel<out T : AbstractFunTileService<*>>(
  val icon: Icon? = null,
  val iconRes: Int? = null,
  val label: String? = null,
  val labelRes: Int? = null,
  val description: String? = null,
  val descriptionRes: Int? = null,
  val status: Status = Status.UNAVAILABLE,
  val onTileClicked: () -> Unit = {}
) {
  enum class Status {
    UNAVAILABLE, ACTIVE, INACTIVE
  }
}

fun Boolean.toTileStatus() = if (this) TileModel.Status.ACTIVE else TileModel.Status.INACTIVE

@Provide
class TileModuleElementModule<@Spread T : Model<TileModel<S>>, S : AbstractFunTileService<*>> {
  @Provide inline fun element(
    serviceClass: KClass<S>,
    provider: T
  ): Pair<TileId, Model<TileModel<*>>> = TileId(serviceClass) to provider as Model<TileModel<*>>

  @Provide inline fun tileId(serviceClass: KClass<S>): TileId = TileId(serviceClass)

  companion object {
    @Provide val defaultElements: Collection<Pair<TileId, Model<TileModel<*>>>>
      get() = emptyList()

    @Provide val defaultTileIds: Collection<TileId> get() = emptyList()
  }
}

object TileScope

@JvmInline value class TileId(val clazz: KClass<*>)

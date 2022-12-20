/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tile

import android.graphics.drawable.Icon
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import kotlin.reflect.KClass

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

object TileModuleElementModule {
  @Provide inline fun <@Spread T : Model<TileModel<S>>, S : AbstractFunTileService<*>> element(
    serviceClass: KClass<S>,
    provider: T
  ): Pair<TileId, Model<TileModel<*>>> = TileId(serviceClass) to provider as Model<TileModel<*>>

  @Provide inline fun <@Spread T : Model<TileModel<S>>, S : AbstractFunTileService<*>> tileId(
    serviceClass: KClass<S>
  ): TileId = TileId(serviceClass)

  @Provide val defaultElements get() = emptyList<Pair<TileId, Model<TileModel<*>>>>()

  @Provide val defaultTileIds get() = emptyList<TileId>()
}

object TileScope

@JvmInline value class TileId(val clazz: KClass<*>)

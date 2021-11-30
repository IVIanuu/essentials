/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tile

import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Provide
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

@Provide
class TileModuleElementModule<@com.ivianuu.injekt.Spread T : @Composable () -> TileModel<S>, S : AbstractFunTileService<*>> {
  @Provide fun element(
    serviceClass: KClass<S>,
    provider: () -> T
  ): Pair<TileId, () -> @Composable () -> TileModel<*>> = TileId(serviceClass) to provider.cast()

  @Provide fun tileId(serviceClass: KClass<S>): TileId = TileId(serviceClass)

  companion object {
    @Provide val defaultElements: Collection<Pair<TileId, () -> @Composable () -> TileModel<*>>>
      get() = emptyList()

    @Provide val defaultTileIds: Collection<TileId> get() = emptyList()
  }
}

object TileScope

@JvmInline value class TileId(val clazz: KClass<*>)

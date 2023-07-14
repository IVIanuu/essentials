/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tile

import android.graphics.drawable.Icon
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import kotlin.reflect.KClass

data class TileModel<out T : AbstractEsTileService<*>>(
  val icon: Icon? = null,
  val label: String? = null,
  val description: String? = null,
  val status: Status = Status.ACTIVE,
  val onClick: () -> Unit = {}
) {
  enum class Status { UNAVAILABLE, ACTIVE, INACTIVE }
}

@Provide object TileModelModule {
  @Provide fun <@Spread T : Model<TileModel<S>>, S : AbstractEsTileService<*>> tileModels(
    serviceClass: KClass<S>,
    model: () -> T
  ): Pair<KClass<AbstractEsTileService<*>>, () -> Model<TileModel<*>>> = (serviceClass to model).cast()

  @Provide val defaultTileModels get() = emptyList<Pair<KClass<AbstractEsTileService<*>>, () -> Model<TileModel<*>>>>()
}

object TileScope

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.tile.FunTileService1
import com.ivianuu.essentials.tile.TileModel
import com.ivianuu.essentials.ui.navigation.Model

fun sampleTileModel() = Model {
  TileModel<FunTileService1>()
}

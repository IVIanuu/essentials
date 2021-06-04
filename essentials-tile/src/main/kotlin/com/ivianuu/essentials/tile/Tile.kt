/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.tile

import android.graphics.drawable.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Optics data class TileModel<out T : AbstractFunTileService<*>>(
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
class TileModuleElementModule<@Spread T : StateFlow<TileModel<S>>, S : AbstractFunTileService<*>> {
  @Provide fun element(
    serviceClass: KClass<S>,
    provider: () -> T
  ): Pair<TileId, () -> StateFlow<TileModel<*>>> = TileId(serviceClass) to provider.cast()

  @Provide fun clazz(serviceClass: KClass<S>): TileId = TileId(serviceClass)
}

typealias TileScope = Scope

@Provide val tileScopeModule =
  ChildScopeModule1<ServiceScope, TileId, TileScope>()

inline class TileId(val clazz: KClass<*>)

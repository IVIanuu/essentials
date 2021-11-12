/*
 * Copyright 2021 Manuel Wrage
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

import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.common.Component
import com.ivianuu.injekt.common.EntryPoint
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
}

@Component interface TileComponent

@EntryPoint<ServiceComponent> interface TileComponentFactory {
  fun tileComponent(tileId: TileId): TileComponent
}

@JvmInline value class TileId(val clazz: KClass<*>)

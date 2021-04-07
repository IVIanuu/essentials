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

import android.graphics.drawable.Icon
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.util.cast
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ServiceGivenScope
import com.ivianuu.injekt.common.ForTypeKey
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.scope.ChildGivenScopeModule1
import com.ivianuu.injekt.scope.DefaultGivenScope

typealias TileStateElement = Pair<TypeKey<AbstractFunTileService>, () -> Store<TileState<*>, TileAction>>

data class TileState<T : AbstractFunTileService>(
    val icon: Icon? = null,
    val iconRes: Int? = null,
    val label: String? = null,
    val labelRes: Int? = null,
    val description: String? = null,
    val descriptionRes: Int? = null,
    val status: Status = Status.UNAVAILABLE
) {
    enum class Status {
        UNAVAILABLE, ACTIVE, INACTIVE
    }
}

fun Boolean.toTileStatus() = if (this) TileState.Status.ACTIVE else TileState.Status.INACTIVE

sealed class TileAction {
    object TileClicked : TileAction()
}

@Given
fun <@Given T : Store<TileState<S>, TileAction>, S : AbstractFunTileService> tileStateElement(
    @Given provider: () -> T,
    @Given serviceKey: TypeKey<S>
): TileStateElement = serviceKey to provider.cast()

typealias TileGivenScope = DefaultGivenScope

@Given
val tileGivenScopeModule = ChildGivenScopeModule1<ServiceGivenScope, TypeKey<*>, TileGivenScope>()

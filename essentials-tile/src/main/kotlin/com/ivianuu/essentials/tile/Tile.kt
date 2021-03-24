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
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ServiceGivenScope
import com.ivianuu.injekt.common.ForTypeKey
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.scope.ChildGivenScopeModule0
import com.ivianuu.injekt.scope.DefaultGivenScope
import com.ivianuu.injekt.scope.GivenScope
import kotlinx.coroutines.flow.StateFlow

typealias TileStateElement = Pair<TypeKey<AbstractFunTileService>, () -> StateFlow<TileState<*>>>

data class TileState<T : AbstractFunTileService>(
    val icon: Icon? = null,
    val iconRes: Int? = null,
    val label: String? = null,
    val labelRes: Int? = null,
    val description: String? = null,
    val descriptionRes: Int? = null,
    val status: Status = Status.Active
) {
    enum class Status {
        Unavailable, Active, Inactive
    }
}

fun Boolean.toTileStatus() = if (this) TileState.Status.Active else TileState.Status.Inactive

sealed class TileAction {
    object TileClicked : TileAction()
}

@Given
fun <@Given T : StateFlow<TileState<S>>, @ForTypeKey S : AbstractFunTileService> tileStateElement(
    @Given provider: () -> T,
): TileStateElement = typeKeyOf<S>() to provider

typealias TileGivenScope = DefaultGivenScope

@Given
val tileGivenScopeModule = ChildGivenScopeModule0<ServiceGivenScope, TileGivenScope>()

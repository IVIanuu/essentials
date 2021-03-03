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
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.common.ForTypeKey
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

typealias TileStateElement = Pair<TypeKey<AbstractFunTileService>,
            (CoroutineScope, Flow<TileAction>) -> StateFlow<TileState>>

data class TileState(
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

@Qualifier
annotation class TileStateBinding<T : AbstractFunTileService>

@Macro
@GivenSetElement
fun <T : @TileStateBinding<S> StateFlow<TileState>, @ForTypeKey S : AbstractFunTileService> tileStateBindingImpl(
    @Given provider: (@Given CoroutineScope, @Given Flow<TileAction>) -> T,
): TileStateElement = typeKeyOf<S>() to provider

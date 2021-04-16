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

package com.ivianuu.essentials.hidenavbar

import android.content.*
import com.ivianuu.essentials.broadcast.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

typealias ForceNavBarVisibleState = Boolean

/**
 * We always wanna show the nav bar on system shut down
 */
@Given
fun systemShutdownForceNavBarVisibleState(
    @Given broadcastsFactory: BroadcastsFactory
): Flow<ForceNavBarVisibleState> = flow {
    broadcastsFactory(Intent.ACTION_SHUTDOWN)
        .onStart { emit(false) }
        .map { true }
}

internal typealias CombinedForceNavBarVisibleState = Boolean

@Given
fun combinedForceNavBarVisibleState(
    @Given forceNavbarVisibleStates: Set<Flow<ForceNavBarVisibleState>>
): Flow<CombinedForceNavBarVisibleState> = combine(
    forceNavbarVisibleStates
        .map { state ->
            state
                .onStart { emit(false) }
        }
) { states ->
    states.any { it }
}

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

package com.ivianuu.essentials.hidenavbar

import android.content.Intent
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@JvmInline value class ForceNavBarVisibleState(val value: Boolean)

/**
 * We always wanna show the nav bar on system shut down
 */
@Provide fun systemShutdownForceNavBarVisibleState(
  broadcastsFactory: BroadcastsFactory
): Flow<ForceNavBarVisibleState> = flow {
  broadcastsFactory(Intent.ACTION_SHUTDOWN)
    .onStart { emit(ForceNavBarVisibleState(false)) }
    .map { ForceNavBarVisibleState(true) }
}

@JvmInline value class CombinedForceNavBarVisibleState(val value: Boolean)

@Provide fun combinedForceNavBarVisibleState(
  forceNavbarVisibleStates: List<Flow<ForceNavBarVisibleState>>
): Flow<CombinedForceNavBarVisibleState> = combine(
  forceNavbarVisibleStates
    .map { state ->
      state
        .onStart { emit(ForceNavBarVisibleState(true)) }
    }
) { states -> CombinedForceNavBarVisibleState(states.any { it.value }) }

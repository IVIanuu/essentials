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

package com.ivianuu.essentials.foreground

import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.d
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

data class ForegroundInfo(val id: Int, val state: ForegroundState)

data class InternalForegroundState(val infos: List<ForegroundInfo>) {
  val isForeground: Boolean get() = infos.any { it.state is ForegroundState.Foreground }
}

@Provide fun internalForegroundState(
  foregroundStates: Set<Flow<ForegroundState>> = emptySet(),
  logger: Logger,
  scope: NamedCoroutineScope<AppScope>,
): @Scoped<AppScope> Flow<InternalForegroundState> = combine(
  foregroundStates
    .mapIndexed { index, foregroundState ->
      foregroundState
        .onStart { emit(ForegroundState.Background) }
        .map { ForegroundInfo(index + 1, it) }
        .distinctUntilChanged()
    }
) { currentForegroundStates -> InternalForegroundState(currentForegroundStates.toList()) }
  .onEach { current -> d { "Internal foreground state changed $current" } }
  .shareIn(scope, SharingStarted.WhileSubscribed(), 1)
  .distinctUntilChanged()

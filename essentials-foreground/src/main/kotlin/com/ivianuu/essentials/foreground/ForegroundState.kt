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

package com.ivianuu.essentials.foreground

import android.app.Notification
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.foreground.ForegroundState.*
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.ForEffect
import com.ivianuu.injekt.Scoped
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

sealed class ForegroundState {
    data class Foreground(val notification: Notification) : ForegroundState()
    object Background : ForegroundState()
}

@Effect
annotation class ForegroundStateBinding {
    companion object {
        @SetElements
        fun <T : Flow<ForegroundState>> intoMap(instance: @ForEffect T): ForegroundStates =
            setOf(instance)
    }
}

typealias ForegroundStates = Set<Flow<ForegroundState>>

data class ForegroundInfo(val id: Int, val state: ForegroundState)

data class InternalForegroundState(val infos: List<ForegroundInfo>) {
    val isForeground: Boolean get() = infos.any { it.state is Foreground }
}

@Scoped(ApplicationComponent::class)
@Binding
fun internalForegroundState(
    foregroundStates: ForegroundStates,
    globalScope: GlobalScope,
    logger: Logger,
): Flow<InternalForegroundState> {
    return combine(
        foregroundStates
            .mapIndexed { index, foregroundState ->
                foregroundState
                    .onStart { emit(Background) }
                    .map { ForegroundInfo(index + 1, it) }
                    .distinctUntilChanged()
            }
    ) { currentForegroundStates -> InternalForegroundState(currentForegroundStates.toList()) }
        .onEach { current ->
            logger.d { "Internal foreground state changed $current" }
        }
        .shareIn(globalScope, SharingStarted.Lazily, 1)
}

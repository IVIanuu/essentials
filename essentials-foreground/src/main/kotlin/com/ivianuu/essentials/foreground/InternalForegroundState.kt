package com.ivianuu.essentials.foreground

import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
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

@Scoped<AppComponent>
@Given
fun internalForegroundState(
    @Given foregroundStates: Set<Flow<ForegroundState>>,
    @Given globalScope: GlobalScope,
    @Given logger: Logger,
): Flow<InternalForegroundState> = combine(
    foregroundStates
        .mapIndexed { index, foregroundState ->
            foregroundState
                .onStart { emit(ForegroundState.Background) }
                .map { ForegroundInfo(index + 1, it) }
                .distinctUntilChanged()
        }
) { currentForegroundStates -> InternalForegroundState(currentForegroundStates.toList()) }
    .onEach { current ->
        logger.d { "Internal foreground state changed $current" }
    }
    .shareIn(globalScope, SharingStarted.Lazily, 1)

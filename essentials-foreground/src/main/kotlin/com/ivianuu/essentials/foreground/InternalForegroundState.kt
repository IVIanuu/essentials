package com.ivianuu.essentials.foreground

import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
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

@Given
fun internalForegroundState(
    @Given foregroundStates: Set<Flow<ForegroundState>> = emptySet(),
    @Given logger: Logger,
    @Given scope: ScopeCoroutineScope<AppGivenScope>,
): @Scoped<AppGivenScope> Flow<InternalForegroundState> = combine(
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
    .shareIn(scope, SharingStarted.Lazily, 1)
    .distinctUntilChanged()

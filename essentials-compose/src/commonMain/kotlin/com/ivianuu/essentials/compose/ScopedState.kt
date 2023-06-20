package com.ivianuu.essentials.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Tag annotation class ScopedState<N> {
  companion object {
    @Provide fun <@Spread T : @ScopedState<N> @Composable () -> S, N, S> scopedState(
      key: TypeKey<T>,
      scope: Scope<N>,
      coroutineScope: ScopedCoroutineScope<N>,
      block: T
    ): @Composable () -> S = {
      scope.scoped<StateFlow<S>>(key) {
        coroutineScope.compositionStateFlow {
          (block as @Composable () -> S).invoke()
        }
      }
        .collectAsState()
        .value
    }
  }
}

fun <T> (@Composable () -> T).asFlow(): Flow<T> = compositionFlow { this() }

fun <T> Flow<T>.asComposable(initial: T): @Composable () -> T = { collectAsState(initial).value }

fun <T> StateFlow<T>.asComposable(): @Composable () -> T = asComposable(value)

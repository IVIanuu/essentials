package com.ivianuu.essentials.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import com.ivianuu.essentials.compose.launchComposition
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey

fun interface ScopeComposition<N> : ExtensionPoint<ScopeComposition<N>> {
  @Composable operator fun invoke()
}

fun interface ScopeCompositionRunner<N> {
  operator fun invoke()
}

context(Logger) @Provide fun <N> scopeCompositionRunner(
  nameKey: TypeKey<N>,
  scope: ScopedCoroutineScope<N>,
  compositions: () -> List<ExtensionPointRecord<ScopeComposition<N>>>
) = ScopeCompositionRunner<N> {
  scope.launchComposition {
    DisposableEffect(true) {
      log { "${nameKey.value} launch scope compositions" }
      onDispose { log { "${nameKey.value} dispose scope compositions" } }
    }

    compositions()
      .sortedWithLoadingOrder()
      .forEach { record ->
        key(record) {
          record.instance()
        }
      }
  }
}

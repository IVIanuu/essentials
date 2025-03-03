/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import androidx.compose.ui.util.fastForEach
import com.ivianuu.essentials.*
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.logging.*
import injekt.*
import kotlin.reflect.*

fun interface ScopeInitializer<N : Any> : ExtensionPoint<ScopeInitializer<N>> {
  fun initialize()
}

interface ScopeInitializerRunner<N : Any> : ScopeObserver<N>

@Provide inline fun <N : Any> scopeInitializerRunner(
  crossinline name: () -> KClass<N>,
  crossinline initializers: (Scope<N>) -> List<ExtensionPointRecord<ScopeInitializer<N>>>,
  crossinline logger: (Scope<N>) -> Logger
): ScopeInitializerRunner<N> = object : ScopeInitializerRunner<N> {
  override fun onEnter(scope: Scope<N>) {
    initializers(scope.cast())
      .sortedWithLoadingOrder()
      .fastForEach {
        logger(scope.cast()).d { "${name().simpleName} initialize ${it.key}" }
        it.instance.initialize()
      }
  }
}

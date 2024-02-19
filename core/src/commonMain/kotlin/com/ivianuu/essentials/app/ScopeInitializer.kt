/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.Scope
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

fun interface ScopeInitializer<N> : () -> Unit, ExtensionPoint<ScopeInitializer<N>>

interface ScopeInitializerRunner<N> : ScopeObserver<N>

@Provide inline fun <N> scopeInitializerRunner(
  crossinline nameKey: () -> TypeKey<N>,
  crossinline initializers: (Scope<N>) -> List<ExtensionPointRecord<ScopeInitializer<N>>>,
  crossinline logger: (Scope<N>) -> Logger
): ScopeInitializerRunner<N> = object : ScopeInitializerRunner<N> {
  override fun onEnter(scope: Scope<N>) {
    initializers(scope.cast())
      .sortedWithLoadingOrder()
      .forEach {
        logger(scope.cast()).d { "${nameKey().value} initialize ${it.key.value}" }
        it.instance()
      }
  }
}

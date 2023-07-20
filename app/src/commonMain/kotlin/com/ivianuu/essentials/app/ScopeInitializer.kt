/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.ExtensionPoint
import com.ivianuu.essentials.ExtensionPointRecord
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.ScopeObserver
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.sortedWithLoadingOrder
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey

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
        logger(scope.cast()).log { "${nameKey().value} initialize ${it.key.value}" }
        it.instance()
      }
  }
}

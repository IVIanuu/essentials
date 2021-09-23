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

package com.ivianuu.essentials.app

import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.scope.NamedScopeObserver
import com.ivianuu.injekt.scope.Scope

typealias ScopeInitializer<S> = () -> Unit

@Provide fun <@Spread T : ScopeInitializer<S>, S : Scope> scopeInitializerElement(
  factory: () -> T,
  key: TypeKey<T>,
  loadingOrder: LoadingOrder<T> = LoadingOrder()
): ScopeInitializerElement<S> = ScopeInitializerElement(key, factory, loadingOrder)

data class ScopeInitializerElement<S>(
  val key: TypeKey<ScopeInitializer<S>>,
  val factory: () -> ScopeInitializer<S>,
  val loadingOrder: LoadingOrder<out ScopeInitializer<S>>
) {
  companion object {
    @Provide val descriptor = object : LoadingOrder.Descriptor<ScopeInitializerElement<*>> {
      override fun key(item: ScopeInitializerElement<*>) = item.key

      override fun loadingOrder(item: ScopeInitializerElement<*>) = item.loadingOrder
    }
  }
}

@Provide fun <S : Scope> scopeInitializerRunner(
  initializers: Set<ScopeInitializerElement<S>> = emptySet(),
  logger: Logger,
  scopeKey: TypeKey<S>,
  workerRunner: ScopeWorkerRunner<S>
): NamedScopeObserver<S> = object : NamedScopeObserver<S> {
  override fun init() {
    initializers
      .sortedWithLoadingOrder()
      .forEach {
        log { "${scopeKey.value} initialize ${it.key.value}" }
        it.factory()()
      }
    workerRunner()
  }

  override fun dispose() {
  }
}

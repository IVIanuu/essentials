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
import com.ivianuu.injekt.common.Component
import com.ivianuu.injekt.common.ComponentObserver
import com.ivianuu.injekt.common.TypeKey

typealias ScopeInitializer<C> = () -> Unit

@Provide fun <@Spread T : ScopeInitializer<C>, C : @Component Any> scopeInitializerElement(
  factory: () -> T,
  key: TypeKey<T>,
  loadingOrder: LoadingOrder<T> = LoadingOrder()
): ScopeInitializerElement<C> = ScopeInitializerElement(key, factory, loadingOrder)

data class ScopeInitializerElement<C : @Component Any>(
  val key: TypeKey<ScopeInitializer<C>>,
  val factory: () -> ScopeInitializer<C>,
  val loadingOrder: LoadingOrder<out ScopeInitializer<C>>
) {
  companion object {
    @Provide val descriptor = object : LoadingOrder.Descriptor<ScopeInitializerElement<*>> {
      override fun key(item: ScopeInitializerElement<*>) = item.key

      override fun loadingOrder(item: ScopeInitializerElement<*>) = item.loadingOrder
    }
  }
}

@Provide fun <C : @Component Any> scopeInitializerRunner(
  componentKey: TypeKey<C>,
  initializers: Set<ScopeInitializerElement<C>> = emptySet(),
  logger: Logger,
  workerRunner: ScopeWorkerRunner<C>
): ComponentObserver<C> = object : ComponentObserver<C> {
  override fun init() {
    initializers
      .sortedWithLoadingOrder()
      .forEach {
        log { "${componentKey.value} initialize ${it.key.value}" }
        it.factory()()
      }
    workerRunner()
  }

  override fun dispose() {
  }
}

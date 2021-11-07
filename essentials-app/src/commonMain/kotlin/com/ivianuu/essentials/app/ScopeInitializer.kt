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

import com.ivianuu.essentials.logging.Log
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.Component
import com.ivianuu.injekt.common.ComponentObserver
import com.ivianuu.injekt.common.TypeKey

@Tag annotation class ScopeInitializerTag<K : ScopeInitializerKey<*>>
typealias ScopeInitializer<K> = @ScopeInitializerTag<K> () -> Unit
interface ScopeInitializerKey<C : @Component Any>

@Provide fun <@Spread T : ScopeInitializer<K>, K : ScopeInitializerKey<C>, C : @Component Any> scopeInitializerElement(
  factory: () -> T,
  key: TypeKey<K>,
  loadingOrder: LoadingOrder<T> = LoadingOrder()
): ScopeInitializerElement<C> = ScopeInitializerElement(key, factory, loadingOrder)

data class ScopeInitializerElement<C : @Component Any>(
  val key: TypeKey<*>,
  val factory: () -> ScopeInitializer<*>,
  val loadingOrder: LoadingOrder<out ScopeInitializer<*>>
) {
  companion object {
    @Provide val descriptor = object : LoadingOrder.Descriptor<ScopeInitializerElement<*>> {
      override fun key(item: ScopeInitializerElement<*>) = item.key

      override fun loadingOrder(item: ScopeInitializerElement<*>) = item.loadingOrder
    }
  }
}

@Provide @Log fun <C : @Component Any> scopeInitializerRunner(
  componentKey: TypeKey<C>,
  initializers: List<ScopeInitializerElement<C>> = emptyList(),
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

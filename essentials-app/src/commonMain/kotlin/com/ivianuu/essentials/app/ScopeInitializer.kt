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
import com.ivianuu.injekt.common.Eager
import com.ivianuu.injekt.common.TypeKey

fun interface ScopeInitializer<N> : () -> Unit

@Provide fun <@Spread T : ScopeInitializer<N>, N> scopeInitializerElement(
  initializer: T,
  key: TypeKey<T>,
  loadingOrder: LoadingOrder<T> = LoadingOrder()
): ScopeInitializerElement<N> = ScopeInitializerElement(key, initializer, loadingOrder)

data class ScopeInitializerElement<N>(
  val key: TypeKey<*>,
  val initializer: ScopeInitializer<*>,
  val loadingOrder: LoadingOrder<out ScopeInitializer<*>>
) {
  companion object {
    @Provide val descriptor = object : LoadingOrder.Descriptor<ScopeInitializerElement<*>> {
      override fun key(item: ScopeInitializerElement<*>) = item.key

      override fun loadingOrder(item: ScopeInitializerElement<*>) = item.loadingOrder
    }

    @Provide fun <N> defaultElements() = emptyList<ScopeInitializerElement<N>>()
  }
}

class ScopeInitializerRunner<N> @Provide @Eager<N> constructor(
  nameKey: TypeKey<N>,
  initializers: List<ScopeInitializerElement<N>>,
  workerRunner: ScopeWorkerRunner<N>,
  L: Logger
) {
  init {
    initializers
      .sortedWithLoadingOrder()
      .forEach {
        log { "${nameKey.value} initialize ${it.key.value}" }
        it.initializer()
      }
    workerRunner()
  }
}

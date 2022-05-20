/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.TypeKey

interface Service<T : Service<T>>

data class ServiceElement<T : Service<*>>(
  val key: TypeKey<T>,
  val instance: T,
  val loadingOrder: LoadingOrder<T>
) {
  companion object {
    @Provide val treeDescriptor = object : LoadingOrder.Descriptor<ServiceElement<*>> {
      override fun key(item: ServiceElement<*>) = item.key
      override fun loadingOrder(item: ServiceElement<*>) = item.loadingOrder
    }

    @Provide fun <@Spread T : Service<B>, B : Service<*>> serviceElement(
      initializer: T,
      key: TypeKey<T>,
      loadingOrder: LoadingOrder<T> = LoadingOrder()
    ): ServiceElement<B> = ServiceElement(key, initializer, loadingOrder) as ServiceElement<B>

    @Provide fun <T : Service<*>> defaultServices(): List<ServiceElement<T>> = emptyList()
  }
}

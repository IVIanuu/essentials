/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.TypeKey

interface ExtensionPoint<T : ExtensionPoint<T>>

data class ExtensionPointRecord<T : ExtensionPoint<*>>(
  val key: TypeKey<T>,
  val instance: T,
  val loadingOrder: LoadingOrder<T>
) {
  companion object {
    @Provide val loadingOrderDescriptor = object : LoadingOrder.Descriptor<ExtensionPointRecord<*>> {
      override fun key(x: ExtensionPointRecord<*>): TypeKey<*> = x.key
      override fun loadingOrder(x: ExtensionPointRecord<*>): LoadingOrder<*> = x.loadingOrder
    }

    @Provide fun <@Spread T : ExtensionPoint<B>, B : ExtensionPoint<*>> record(
      initializer: T,
      key: TypeKey<T>,
      loadingOrder: LoadingOrder<T> = LoadingOrder()
    ): ExtensionPointRecord<B> = ExtensionPointRecord(key, initializer, loadingOrder).cast()

    @Provide fun <T : ExtensionPoint<*>> defaultRecords(): List<ExtensionPointRecord<T>> = emptyList()
  }
}

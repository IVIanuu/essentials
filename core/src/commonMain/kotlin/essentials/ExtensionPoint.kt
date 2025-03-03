/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials

import injekt.*
import kotlin.reflect.*

interface ExtensionPoint<T : ExtensionPoint<T>>

data class ExtensionPointRecord<T : ExtensionPoint<*>>(
  val key: KClass<T>,
  val instance: T,
  val loadingOrder: LoadingOrder<T>
) {
  @Provide companion object {
    @Provide val loadingOrderDescriptor = object : LoadingOrder.Descriptor<ExtensionPointRecord<*>> {
      override fun key(x: ExtensionPointRecord<*>): KClass<*> = x.key
      override fun loadingOrder(x: ExtensionPointRecord<*>): LoadingOrder<*> = x.loadingOrder
    }

    @Provide fun <@AddOn T : ExtensionPoint<B>, B : ExtensionPoint<*>> record(
      initializer: T,
      key: KClass<T>,
      loadingOrder: LoadingOrder<T> = LoadingOrder()
    ): ExtensionPointRecord<B> = ExtensionPointRecord(key, initializer, loadingOrder).cast()

    @Provide fun <T : ExtensionPoint<*>> defaultRecords(): List<ExtensionPointRecord<T>> = emptyList()
  }
}

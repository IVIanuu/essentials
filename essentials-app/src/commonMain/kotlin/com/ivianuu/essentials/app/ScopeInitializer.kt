package com.ivianuu.essentials.app

import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.scope.*

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
): com.ivianuu.injekt.scope.ScopeInitializer<S> = {
  initializers
    .sortedWithLoadingOrder()
    .forEach {
      d { "${scopeKey.value} initialize ${it.key.value}" }
      it.factory()()
    }
  workerRunner()
}

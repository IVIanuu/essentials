package com.ivianuu.essentials.app

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.scope.Scope
import com.ivianuu.injekt.scope.scoped

@Tag annotation class Eager<S : Scope> {
  companion object {
    @Provide class Module<@com.ivianuu.injekt.Spread T : @Eager<S> U, U : Any, S : Scope> {
      @Provide inline fun scopedValue(
        factory: () -> T,
        scope: S,
        key: TypeKey<U>
      ): U = scoped(key = key, computation = factory)

      @Provide inline fun initializer(crossinline factory: () -> U): ScopeWorker<S> = { factory() }
    }
  }
}

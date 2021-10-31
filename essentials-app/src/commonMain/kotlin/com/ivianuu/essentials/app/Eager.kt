package com.ivianuu.essentials.app

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.Component
import com.ivianuu.injekt.common.ComponentObserver
import com.ivianuu.injekt.common.Scoped

@Tag annotation class Eager<C : @Component Any> {
  companion object {
    @Provide class Module<@com.ivianuu.injekt.Spread T : @Eager<C> U, U : Any, C : @Component Any> {
      @Provide @Scoped<C> inline fun scopedValue(factory: () -> T): U = factory()

      @Provide inline fun initializer(
        crossinline factory: () -> U
      ): ComponentObserver<C> = object : ComponentObserver<C> {
        override fun init() {
          factory()
        }
      }
    }
  }
}

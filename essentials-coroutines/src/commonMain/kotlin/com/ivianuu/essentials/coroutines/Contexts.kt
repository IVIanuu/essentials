/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.di.*
import kotlin.coroutines.*

@JvmInline value class DefaultContext(override val _value: Any?) : Tag<CoroutineContext> {
  companion object {
    @Provide inline val context: DefaultContext
      get() = DefaultContext(Dispatchers.Default)
  }
}

@JvmInline value class MainContext(override val _value: Any?) : Tag<CoroutineContext> {
  companion object {
    @Provide inline val context: MainContext
      get() = MainContext(Dispatchers.Main)
  }
}

@JvmInline value class ImmediateMainContext(override val _value: Any?) : Tag<CoroutineContext> {
  companion object {
    @Provide inline val context: ImmediateMainContext
      get() = ImmediateMainContext(Dispatchers.Main.immediate)
  }
}

@JvmInline value class IOContext(override val _value: Any?) : Tag<CoroutineContext>

expect object IOInjectables {
  @Provide val context: IOContext
}

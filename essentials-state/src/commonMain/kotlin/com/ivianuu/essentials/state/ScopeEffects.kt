/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.state

import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

fun <T> Scope<*>.scopeKeyed(
  key: @ScopeKey Any,
  vararg args: Any?,
  init: () -> T
): T {
  // todo use key directly once supported
  val holder = this(TypeKey(key.toString())) { ScopeValueHolder(args, init()) }
  if (!holder.args.contentEquals(args)) {
    holder.value = init()
  }
  return holder.value as T
}

@Tag annotation class ScopeKey {
  companion object {
    @Provide fun default(sourceKey: SourceKey): @ScopeKey Any = sourceKey.value
  }
}

private class ScopeValueHolder(var args: Array<*>, var value: Any?) : Disposable {
  override fun dispose() {
    (value as? Disposable)?.dispose()
  }
}

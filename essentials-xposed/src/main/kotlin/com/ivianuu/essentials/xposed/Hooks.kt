package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Provide

fun interface Hooks {
  operator fun XposedContext.invoke()
}

@Provide val defaultHooks: Collection<Hooks> get() = emptyList()

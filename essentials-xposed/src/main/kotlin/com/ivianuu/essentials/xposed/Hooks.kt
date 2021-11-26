package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Provide

fun interface Hooks : (@Provide XposedContext) -> Unit

@Provide val defaultHooks: Collection<Hooks> get() = emptyList()

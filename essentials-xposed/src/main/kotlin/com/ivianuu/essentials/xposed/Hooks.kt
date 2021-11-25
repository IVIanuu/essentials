package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag

@Tag annotation class HooksTag
typealias Hooks = @HooksTag XposedContext.() -> Unit

@Provide val defaultHooks: Collection<Hooks> get() = emptyList()

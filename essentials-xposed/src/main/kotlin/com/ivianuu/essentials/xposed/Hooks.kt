package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Tag

@Tag annotation class HooksTag
typealias Hooks = @HooksTag XposedContext.() -> Unit

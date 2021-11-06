package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Provide

@JvmInline value class IsXposedRunning(val value: Boolean)

@Provide val isXposedRunning: IsXposedRunning get() = IsXposedRunning(false)

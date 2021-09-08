package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Provide

typealias IsXposedRunning = Boolean

@Provide val isXposedRunning: IsXposedRunning get() = false

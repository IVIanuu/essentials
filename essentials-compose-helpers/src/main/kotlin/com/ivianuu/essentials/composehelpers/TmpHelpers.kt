package com.ivianuu.essentials.composehelpers

inline fun <T> exec(block: () -> T): T = block()

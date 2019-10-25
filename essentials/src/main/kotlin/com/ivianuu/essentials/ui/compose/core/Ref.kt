package com.ivianuu.essentials.ui.compose.core

import androidx.compose.memo

fun <T> ref(init: () -> T) = memo { Ref(init()) }

fun <T> refFor(vararg inputs: Any?, init: () -> T) =
    memo(*inputs) { Ref(init()) }

class Ref<T>(var value: T)
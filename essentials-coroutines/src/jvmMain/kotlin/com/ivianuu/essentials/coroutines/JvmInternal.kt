package com.ivianuu.essentials.coroutines

@PublishedApi internal actual inline fun <T> synchronized(lock: Any, block: () -> T): T =
  kotlin.synchronized(lock, block)

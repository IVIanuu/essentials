package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

inline fun <T> Flow<T>.collectIn(
    scope: CoroutineScope,
    crossinline action: suspend (T) -> Unit
) = scope.launch { collect(action) }

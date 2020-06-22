package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// todo better name
fun <T> flowNever(): Flow<T> = flow {
    awaitCancellation()
}

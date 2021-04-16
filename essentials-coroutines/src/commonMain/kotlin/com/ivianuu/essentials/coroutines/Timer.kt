package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.coroutineContext
import kotlin.time.*

@OptIn(ExperimentalTime::class)
fun timer(duration: Duration): Flow<Long> = flow {
    var value = 0L
    while (coroutineContext.isActive) {
        emit(value++)
        delay(duration)
    }
}

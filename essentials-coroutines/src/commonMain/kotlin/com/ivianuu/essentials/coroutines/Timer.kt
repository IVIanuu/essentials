package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun timer(duration: Duration): Flow<Long> = flow {
    var value = 0L
    while (coroutineContext.isActive) {
        emit(value)
        delay(duration)
    }
}

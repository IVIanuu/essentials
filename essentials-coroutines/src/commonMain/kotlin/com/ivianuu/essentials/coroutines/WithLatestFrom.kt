package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

private object NULL

@Suppress("UNCHECKED_CAST")
fun <A, B> Flow<A>.withLatestFrom(other: Flow<B>): Flow<Pair<A, B>> = channelFlow {
    val otherState = MutableStateFlow<Any?>(NULL)
    par(
        {
            other.collect(otherState)
        },
        {
            this@withLatestFrom
                .collect { a ->
                    val b = otherState.first { it !== NULL } as B
                    offer(a to b)
                }
        }
    )
}

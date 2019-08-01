package com.ivianuu.essentials.util

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

fun <T> flowOf(block: suspend () -> T) = flow { emit(block()) }

fun <T> mergeFlows(vararg flows: Flow<T>): Flow<T> = channelFlow {
    coroutineScope {
        for (f in flows) {
            launch {
                f.collect { channel.send(it) }
            }
        }
    }
}

fun <T> mergeFlows(flows: Iterable<Flow<T>>): Flow<T> = channelFlow {
    coroutineScope {
        for (f in flows) {
            launch {
                f.collect { channel.send(it) }
            }
        }
    }
}

fun <T> mergeFlows(flows: Sequence<Flow<T>>): Flow<T> = channelFlow {
    coroutineScope {
        for (f in flows) {
            launch {
                f.collect { channel.send(it) }
            }
        }
    }
}
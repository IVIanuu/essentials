package com.ivianuu.essentials.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

// todo better name
fun <T> flowNever(): Flow<T> = flow {
    while (true) {
        delay(Long.MAX_VALUE)
    }
}

fun <T> flowOf(block: suspend () -> T) = flow { emit(block()) }

fun <T1, T2> combine(flow: Flow<T1>, flow2: Flow<T2>): Flow<Pair<T1, T2>> {
    return combine(
        flow = flow,
        flow2 = flow2,
        transform = { item, item2 -> Pair(item, item2) }
    )
}

fun <T1, T2, T3> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>
): Flow<Triple<T1, T2, T3>> {
    return combine(
        flow = flow,
        flow2 = flow2,
        flow3 = flow3,
        transform = { item, item2, item3 -> Triple(item, item2, item3) }
    )
}

fun <T> merge(vararg flows: Flow<T>): Flow<T> =
    flowOf(*flows).flattenMerge(concurrency = flows.size)

fun <T> merge(flows: Iterable<Flow<T>>): Flow<T> {
    val flowsList = flows.toList()
    return flowsList.asFlow().flattenMerge(concurrency = flowsList.size)
}

fun <T> merge(flows: Sequence<Flow<T>>): Flow<T> {
    val flowsList = flows.toList()
    return flowsList.asFlow().flattenMerge(concurrency = flowsList.size)
}
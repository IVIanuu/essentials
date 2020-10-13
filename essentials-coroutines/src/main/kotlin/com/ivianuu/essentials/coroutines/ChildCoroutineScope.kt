package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

fun CoroutineScope.childCoroutineScope(
    context: CoroutineContext = EmptyCoroutineContext,
): CoroutineScope = CoroutineScope(Job(parent = coroutineContext[Job]) + context)

suspend fun childCoroutineScope(context: CoroutineContext = EmptyCoroutineContext): CoroutineScope =
    CoroutineScope(Job(parent = coroutineContext[Job]!!) + context)

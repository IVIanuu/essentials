package com.ivianuu.essentials.ui.compose.coroutines

import androidx.compose.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun coroutineScope(context: () -> CoroutineContext = { Dispatchers.Main }) =
    effectOf<CoroutineScope> {
        val coroutineScope = +memo { CoroutineScope(context = context() + Job()) }
        +onDispose { coroutineScope.coroutineContext[Job]!!.cancel() }
        return@effectOf coroutineScope
    }

fun launchOnActive(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) = effectOf<Unit> {
    val coroutineScope = +coroutineScope()
    +com.ivianuu.essentials.ui.compose.core.onActive {
        coroutineScope.launch(
            context,
            start,
            block
        )
    }
}

fun launchOnActive(
    vararg inputs: Any?,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) = effectOf<Unit> {
    val coroutineScope = +coroutineScope()
    +com.ivianuu.essentials.ui.compose.core.onActive(*inputs) {
        coroutineScope.launch(
            context,
            start,
            block
        )
    }
}

fun launchOnPreCommit(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) = effectOf<Unit> {
    val coroutineScope = +coroutineScope()
    +onPreCommit { coroutineScope.launch(context, start, block) }
}

fun launchOnPreCommit(
    vararg inputs: Any?,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) = effectOf<Unit> {
    val coroutineScope = +coroutineScope()
    +onPreCommit(*inputs) { coroutineScope.launch(context, start, block) }
}

fun launchOnCommit(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) = effectOf<Unit> {
    val coroutineScope = +coroutineScope()
    +onCommit { coroutineScope.launch(context, start, block) }
}

fun launchOnCommit(
    vararg inputs: Any?,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) = effectOf<Unit> {
    val coroutineScope = +coroutineScope()
    +onCommit(*inputs) { coroutineScope.launch(context, start, block) }
}

@BuilderInference
fun <T> load(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
) = effectOf<T?> {
    +load(
        placeholder = null,
        context = context,
        start = start,
        block = block
    )
}

fun <T> load(
    placeholder: T,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
) = effectOf<T> {
    val state = +state { placeholder }
    launchOnActive(context = context, start = start) {
        state.value = block()
    }
    return@effectOf state.value
}

fun <T> flow(flow: Flow<T>) = flow(null, flow)

fun <T> flow(
    placeholder: T,
    flow: Flow<T>
) = effectOf<T> {
    val state = +state { placeholder }
    val coroutineScope = +coroutineScope()
    +onActive {
        flow
            .onEach { state.value = it }
            .launchIn(coroutineScope)
    }
    return@effectOf state.value
}
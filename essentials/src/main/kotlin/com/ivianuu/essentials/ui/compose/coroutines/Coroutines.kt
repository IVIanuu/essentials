package com.ivianuu.essentials.ui.compose.coroutines

import androidx.compose.Effect
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.memo
import androidx.compose.onActive
import androidx.compose.onCommit
import androidx.compose.onDispose
import androidx.compose.onPreCommit
import androidx.compose.state
import androidx.ui.core.CoroutineContextAmbient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun coroutineScope(context: Effect<CoroutineContext> = coroutineContext()) =
    effectOf<CoroutineScope> {
        val coroutineScope = +memo { CoroutineScope(context = +context + Job()) }
        +onDispose { coroutineScope.coroutineContext[Job]!!.cancel() }
        return@effectOf coroutineScope
    }

fun coroutineContext() = effectOf<CoroutineContext> {
    +ambient(CoroutineContextAmbient)
}

fun launchOnActive(
    block: suspend CoroutineScope.() -> Unit
) = effectOf<Unit> {
    val coroutineScope = +coroutineScope()
    +onActive {
        coroutineScope.launch(block = block)
    }
}

fun launchOnActive(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> Unit
) = effectOf<Unit> {
    val coroutineScope = +coroutineScope()
    +com.ivianuu.essentials.ui.compose.core.onActive(*inputs) {
        coroutineScope.launch(block = block)
    }
}

fun launchOnPreCommit(
    block: suspend CoroutineScope.() -> Unit
) = effectOf<Unit> {
    val coroutineScope = +coroutineScope()
    +onPreCommit { coroutineScope.launch(block = block) }
}

fun launchOnPreCommit(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> Unit
) = effectOf<Unit> {
    val coroutineScope = +coroutineScope()
    +onPreCommit(*inputs) { coroutineScope.launch(block = block) }
}

fun launchOnCommit(
    block: suspend CoroutineScope.() -> Unit
) = effectOf<Unit> {
    val coroutineScope = +coroutineScope()
    +onCommit { coroutineScope.launch(block = block) }
}

fun launchOnCommit(
    vararg inputs: Any?,
    block: suspend CoroutineScope.() -> Unit
) = effectOf<Unit> {
    val coroutineScope = +coroutineScope()
    +onCommit(*inputs) { coroutineScope.launch(block = block) }
}

@BuilderInference
fun <T> load(block: suspend CoroutineScope.() -> T) = effectOf<T?> {
    +load(
        placeholder = null,
        block = block
    )
}

fun <T> load(
    placeholder: T,
    block: suspend CoroutineScope.() -> T
) = effectOf<T> {
    val state = +state { placeholder }
    +launchOnActive { state.value = block() }
    return@effectOf state.value
}

fun <T> collect(flow: Flow<T>) = collect(null, flow)

fun <T> collect(
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
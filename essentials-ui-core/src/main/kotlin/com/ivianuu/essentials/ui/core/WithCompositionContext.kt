package com.ivianuu.essentials.ui.core

import androidx.compose.runtime.*
import com.ivianuu.essentials.*

@Composable
fun <R> withCompositionContext(context: CompositionContext, block: @Composable () -> R): R {
    val compositionLocals = remember(context) {
        context.javaClass.declaredMethods
            .first { it.name == "getCompositionLocalScope\$runtime_release" }
            .invoke(context)
            .cast<Map<CompositionLocal<Any?>, State<Any?>>>()
            .map { (it.key as ProvidableCompositionLocal<Any?>).provides(it.value.value) }
            .toTypedArray()
    }
    currentComposer.startProviders(compositionLocals)
    val result = block()
    currentComposer.endProviders()
    return result
}

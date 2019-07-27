package com.ivianuu.essentials.ui.compose.core

import androidx.compose.Composer
import androidx.compose.Effect

inline class WidgetComposition(val composer: Composer<Widget>) {

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun <V> Effect<V>.unaryPlus(): V = resolve(
        this@WidgetComposition.composer, sourceLocation().hashCode()
    )

    inline fun <T : Widget> emit(noinline ctor: () -> T) = emit(sourceLocation(), ctor)

    fun <T : Widget> emit(
        key: Any,
        ctor: () -> T
    ) {
        with(composer) {
            startNode(key)
            val element = if (inserting) ctor().also { emitNode(it) }
            else useNode()
            with(element) { compose() }
            endNode()
        }
    }

}
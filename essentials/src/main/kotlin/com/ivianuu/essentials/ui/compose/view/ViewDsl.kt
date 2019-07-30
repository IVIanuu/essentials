package com.ivianuu.essentials.ui.compose.view

import android.content.Context
import android.view.View
import androidx.compose.ViewComposition
import androidx.compose.ViewUpdater
import androidx.compose.sourceLocation

inline fun <reified T : View> ViewComposition.View(noinline block: ViewDsl<T>.() -> Unit) {
    View(
        key = sourceLocation(),
        ctor = { T::class.java.getDeclaredConstructor(Context::class.java).newInstance(it) },
        block = block
    )
}

fun <T : View> ViewComposition.View(
    key: Any,
    ctor: (Context) -> T,
    block: ViewDsl<T>.() -> Unit
) {
    with(composer) {
        startNode(key)
        val node = (if (inserting) ctor(context).also { emitNode(it) } else useNode()) as T
        ViewDsl(this@View, node).block()
        endNode()
    }

}

open class ViewDsl<T : View>(
    val composition: ViewComposition,
    val node: T
) {

    val updater by lazy(LazyThreadSafetyMode.NONE) {
        ViewUpdater(composition.composer, node)
    }

    inline fun update(block: ViewUpdater<T>.() -> Unit) {
        with(updater) { block() }
    }

}

inline fun <T : View, reified V> ViewDsl<T>.set(value: V, block: T.(V) -> Unit) {
    update { set(value, block) }
}
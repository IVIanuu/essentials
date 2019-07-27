package com.ivianuu.essentials.ui.compose.core

import androidx.compose.Applier
import androidx.compose.ApplyAdapter
import androidx.compose.Composer
import androidx.compose.Effect
import androidx.compose.FrameManager
import androidx.compose.Recomposer
import androidx.compose.SlotTable
import androidx.compose.Stack

class WidgetComposer(
    val root: Any,
    recomposer: Recomposer
) : Composer<Any>(
    SlotTable(),
    Applier(root, WidgetApplyAdapter() as ApplyAdapter<Any>),
    recomposer
) {

    init {
        FrameManager.ensureStarted()
    }

}

internal class WidgetApplyAdapter : ApplyAdapter<WidgetParent> {

    private data class PendingInsert(val index: Int, val instance: Any)

    private val pendingInserts = Stack<PendingInsert>()

    override fun WidgetParent.start(instance: WidgetParent) {

    }

    override fun WidgetParent.insertAt(index: Int, instance: WidgetParent) {
        pendingInserts.push(PendingInsert(index, instance))
    }

    override fun WidgetParent.move(from: Int, to: Int, count: Int) {
        moveChild(from, to, count)
    }

    override fun WidgetParent.removeAt(index: Int, count: Int) {
        removeChild(index, count)
    }

    override fun WidgetParent.end(instance: WidgetParent, parent: WidgetParent) {
        if (pendingInserts.isNotEmpty()) {
            val pendingInsert = pendingInserts.peek()
            if (pendingInsert.instance == instance) {
                val index = pendingInsert.index
                pendingInserts.pop()
                insertChild(index, instance as Widget<*>)
                return
            }
        }

        parent.updateChild(instance as Widget<*>)
    }

}

inline class WidgetComposition(val composer: Composer<Any>) {

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun <V> Effect<V>.unaryPlus(): V = resolve(
        this@WidgetComposition.composer, sourceLocation().hashCode()
    )

    inline fun <T : Widget<*>> emit(noinline ctor: () -> T) = emit(sourceLocation(), ctor)

    fun <T : Widget<*>> emit(
        key: Any,
        ctor: () -> T
    ) {
        with(composer) {
            startNode(key)
            val widget = if (inserting) ctor().also { emitNode(it) }
            else useNode()
            with(widget as Widget<*>) { compose() }
            endNode()
        }
    }

}
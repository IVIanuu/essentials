package com.ivianuu.essentials.ui.compose.core

import androidx.compose.Applier
import androidx.compose.ApplyAdapter
import androidx.compose.Composer
import androidx.compose.FrameManager
import androidx.compose.Recomposer
import androidx.compose.SlotTable
import androidx.compose.Stack

class WidgetComposer(
    val root: Widget,
    recomposer: Recomposer
) : Composer<Widget>(
    SlotTable(),
    Applier(root, WidgetApplyAdapter()),
    recomposer
) {

    init {
        FrameManager.ensureStarted()
    }

}

internal class WidgetApplyAdapter : ApplyAdapter<Widget> {

    private data class PendingInsert(val index: Int, val instance: Any)

    private val pendingInserts = Stack<PendingInsert>()

    override fun Widget.start(instance: Widget) {
    }

    override fun Widget.insertAt(index: Int, instance: Widget) {
        pendingInserts.push(PendingInsert(index, instance))
    }

    override fun Widget.move(from: Int, to: Int, count: Int) {
        moveChild(from, to, count)
    }

    override fun Widget.removeAt(index: Int, count: Int) {
        removeChild(index, count)
    }

    override fun Widget.end(instance: Widget, parent: Widget) {
        if (pendingInserts.isNotEmpty()) {
            val pendingInsert = pendingInserts.peek()
            if (pendingInsert.instance == instance) {
                val index = pendingInsert.index
                pendingInserts.pop()
                insertChild(index, instance)
                return
            }
        }

        parent.updateChild(instance)
    }

}
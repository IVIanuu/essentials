package com.ivianuu.essentials.ui.compose.view

import android.view.View
import android.view.ViewGroup
import androidx.compose.adapters.ViewAdapter
import androidx.compose.adapters.getOrAddAdapter
import androidx.compose.adapters.tagKey

class LayoutBuilder : ViewAdapter {
    private var builtLayoutParams: ViewGroup.LayoutParams? = null
    private var dirty = false

    private val blocks = mutableListOf<ViewGroup.LayoutParams.() -> Unit>()

    fun add(block: ViewGroup.LayoutParams.() -> Unit) {
        blocks.add(block)
        dirty = true
    }

    override val id: Int = LayoutParamsId

    override fun didInsert(view: View, parent: ViewGroup) {
        // do nothing
    }

    override fun didUpdate(view: View, parent: ViewGroup) {
        buildAndSet(view, parent)
    }

    override fun willInsert(view: View, parent: ViewGroup) {
        // on first pass we want to make sure and set the layout params *before* the view gets added
        // to the parent
        buildAndSet(view, parent)
    }

    private fun buildAndSet(view: View, parent: ViewGroup) {
        if (!dirty) return
        dirty = false
        val prev = builtLayoutParams

        val lp = prev ?: genDefaultLayoutParams.invoke(parent) as? ViewGroup.LayoutParams
        ?: error("couldn't create default layout params")

        blocks.forEach { it(lp) }

        if (prev == null || blocks.isNotEmpty()) {
            // params have not been set yet, or they've been updated
            view.layoutParams = lp
        }

        blocks.clear()

        builtLayoutParams = lp
    }
}

fun View.getLayoutParamsBuilder() = getOrAddAdapter(LayoutParamsId) { LayoutBuilder() }


private val LayoutParamsId = tagKey("LayoutParams")

private val genDefaultLayoutParams by lazy {
    val method = ViewGroup::class.java.getDeclaredMethod("generateDefaultLayoutParams")
    method.isAccessible = true
    method
}
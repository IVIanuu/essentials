package com.ivianuu.essentials.ui.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author Manuel Wrage (IVIanuu)
 */
abstract class RvModel<H : RvHolder> {

    abstract val id: Long

    abstract val layoutRes: Int

    open val viewType get() = layoutRes

    open fun bind(holder: H) {
    }

    open fun unbind(holder: H) {
    }

    open fun buildView(parent: ViewGroup): View =
        LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)

    abstract fun createNewHolder(): H

    open fun onViewAttachedFromWindow(holder: H) {
    }

    open fun onViewDetachedFromWindow(holder: H) {
    }

    open fun onFailedToRecycleView(holder: H) = true

    open fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int) = 1

}

fun RvModel<*>.addTo(controller: RvController) = apply { controller.add(this) }
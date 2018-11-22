package com.ivianuu.essentials.ui.rv.common.touch

import android.view.View
import com.ivianuu.essentials.ui.rv.RvModel


interface BaseRvTouchCallback<T : RvModel<*>> {

    /**
     * Should return a composite flag which defines the enabled move directions in each state
     * (idle, swiping, dragging) for the given model.
     */
    fun getMovementFlagsForModel(model: T, adapterPosition: Int): Int

    /**
     * Called when the user interaction with a view is over and the view has
     * completed its animation. This is a good place to clear all changes on the view that were done
     * in other previous touch callbacks (such as on touch start, change, release, etc)..
     */
    fun clearView(model: T, itemView: View)
}
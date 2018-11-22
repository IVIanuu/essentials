package com.ivianuu.essentials.ui.rv.common.touch

import android.view.View
import com.ivianuu.essentials.ui.rv.RvModel


interface RvDragCallback<T : RvModel<*>> : BaseRvTouchCallback<T> {

    /**
     * Called when the view switches from an idle state to a dragged state, as the user begins a drag
     * interaction with it. You can use this callback to modify the view to indicate it is being
     * dragged.
     */
    fun onDragStarted(model: T, itemView: View, adapterPosition: Int)

    /**
     * Called after [.onDragStarted] when the dragged view is dropped to
     * a new position. The EpoxyController will be updated automatically for you to reposition the
     * models and notify the RecyclerView of the change.
     */
    fun onModelMoved(fromPosition: Int, toPosition: Int, modelBeingMoved: T, itemView: View)

    /**
     * Called after [.onDragStarted] when the view being dragged is
     * released. If the view was dragged to a new, valid location then [.onModelMoved] will be called before this and the view will settle to the new location.
     * Otherwise the view will animate back to its original position.
     */
    fun onDragReleased(model: T, itemView: View)
}
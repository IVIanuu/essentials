/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.list.touch

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.list.ListController
import com.ivianuu.essentials.ui.list.ListModel
import com.ivianuu.essentials.ui.list.ListViewHolder

abstract class ListModelTouchCallback<T : ListModel<*>>(
    private val controller: ListController?,
    private val targetModelClass: Class<T>
) : ListTouchHelperCallback(), ListDragCallback<T>, ListSwipeCallback<T> {
    private var holderBeingDragged: ListViewHolder? = null
    private var holderBeingSwiped: ListViewHolder? = null

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ListViewHolder): Int {
        val model = viewHolder.model

        // If multiple touch callbacks are registered on the recyclerview (to support combinations of
        // dragging and dropping) then we won't want to enable anything if another
        // callback has a view actively selected.
        val isOtherCallbackActive = (holderBeingDragged == null
                && holderBeingSwiped == null
                && recyclerViewHasSelection(recyclerView))

        return if (!isOtherCallbackActive && isTouchableModel(model!!)) {
            getMovementFlagsForModel(model as T, viewHolder.adapterPosition)
        } else {
            0
        }
    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: ListViewHolder,
        target: ListViewHolder
    ): Boolean {
        // By default we don't allow dropping on a model that isn't a drag target
        return isTouchableModel(target.model!!)
    }

    protected fun isTouchableModel(model: ListModel<*>): Boolean {
        return targetModelClass.isInstance(model)
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: ListViewHolder,
        target: ListViewHolder
    ): Boolean {

        if (controller == null) {
            throw IllegalStateException(
                "A controller must be provided in the constructor if dragging is enabled"
            )
        }

        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        // todo controller.moveModel(fromPosition, toPosition)

        val model = viewHolder.model!!
        if (!isTouchableModel(model)) {
            throw IllegalStateException(
                "A model was dragged that is not a valid target: " + model.javaClass
            )
        }


        onModelMoved(fromPosition, toPosition, model as T, viewHolder.itemView)
        return true
    }

    override fun onModelMoved(
        fromPosition: Int,
        toPosition: Int,
        modelBeingMoved: T,
        itemView: View
    ) {

    }

    override fun onSwiped(viewHolder: ListViewHolder, direction: Int) {
        val model = viewHolder.model!!
        val view = viewHolder.itemView
        val position = viewHolder.adapterPosition

        if (!isTouchableModel(model)) {
            throw IllegalStateException(
                "A model was swiped that is not a valid target: " + model.javaClass
            )
        }


        onSwipeCompleted(model as T, view, position, direction)
    }

    override fun onSwipeCompleted(model: T, itemView: View, position: Int, direction: Int) {

    }

    override fun onSelectedChanged(viewHolder: ListViewHolder, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        val model = viewHolder.model!!
        if (!isTouchableModel(model)) {
            throw IllegalStateException(
                "A model was selected that is not a valid target: " + model.javaClass
            )
        }

        markRecyclerViewHasSelection(viewHolder.itemView.parent as RecyclerView)

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            holderBeingSwiped = viewHolder

            onSwipeStarted(model as T, viewHolder.itemView, viewHolder.adapterPosition)
        } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            holderBeingDragged = viewHolder

            onDragStarted(model as T, viewHolder.itemView, viewHolder.adapterPosition)
        }
    }

    private fun markRecyclerViewHasSelection(recyclerView: RecyclerView) {
        recyclerView.setTag(R.id.epoxy_touch_helper_selection_status, java.lang.Boolean.TRUE)
    }

    private fun recyclerViewHasSelection(recyclerView: RecyclerView): Boolean {
        return recyclerView.getTag(R.id.epoxy_touch_helper_selection_status) != null
    }

    private fun clearRecyclerViewSelectionMarker(recyclerView: RecyclerView) {
        recyclerView.setTag(R.id.epoxy_touch_helper_selection_status, null)
    }

    override fun onSwipeStarted(model: T, itemView: View, adapterPosition: Int) {

    }

    override fun onSwipeReleased(model: T, itemView: View) {

    }

    override fun onDragStarted(model: T, itemView: View, adapterPosition: Int) {

    }

    override fun onDragReleased(model: T, itemView: View) {

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: ListViewHolder) {
        super.clearView(recyclerView, viewHolder)

        clearView(viewHolder.model as T, viewHolder.itemView)

        // If multiple touch helpers are in use, one touch helper can pick up buffered touch inputs
        // immediately after another touch event finishes. This leads to things like a view being
        // selected for drag when another view finishes its swipe off animation. To prevent that we
        // keep the recyclerview marked as having an active selection for a brief period after a
        // touch event ends.
        recyclerView!!.postDelayed(
            { clearRecyclerViewSelectionMarker(recyclerView) },
            TOUCH_DEBOUNCE_MILLIS.toLong()
        )
    }

    override fun clearView(model: T, itemView: View) {

    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: ListViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val model = viewHolder.model!!
        if (!isTouchableModel(model)) {
            throw IllegalStateException(
                "A model was selected that is not a valid target: " + model.javaClass
            )
        }

        val itemView = viewHolder.itemView

        val swipeProgress: Float
        if (Math.abs(dX) > Math.abs(dY)) {
            swipeProgress = dX / itemView.width
        } else {
            swipeProgress = dY / itemView.height
        }

        // Clamp to 1/-1 in the case of side padding where the view can be swiped extra
        val clampedProgress = Math.max(-1f, Math.min(1f, swipeProgress))

        onSwipeProgressChanged(model as T, itemView, clampedProgress, c)
    }

    override fun onSwipeProgressChanged(
        model: T,
        itemView: View,
        swipeProgress: Float,
        canvas: Canvas
    ) {
    }

    companion object {
        private const val TOUCH_DEBOUNCE_MILLIS = 300
    }
}
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
import com.ivianuu.essentials.ui.list.ListAdapter
import com.ivianuu.essentials.ui.list.ListModel
import com.ivianuu.essentials.ui.list.ListViewHolder
import com.ivianuu.essentials.ui.list.moveModel
import com.ivianuu.essentials.ui.list.requireModel
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
abstract class ListModelTouchCallback<T : ListModel<*>>(
    private val targetModelClass: KClass<T>? = null
) : ItemTouchHelper.Callback(), ListDragCallback<T>, ListSwipeCallback<T> {

    private var holderBeingDragged: ListViewHolder? = null
    private var holderBeingSwiped: ListViewHolder? = null

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        viewHolder as ListViewHolder

        val model = viewHolder.requireModel()

        // If multiple touch callbacks are registered on the recyclerview (to support combinations of
        // dragging and dropping) then we won't want to enable anything if another
        // callback has a view actively selected.
        val isOtherCallbackActive = holderBeingDragged == null
                && holderBeingSwiped == null
                && recyclerView.hasSelection

        return if (!isOtherCallbackActive && isTouchableModel(model)) {
            getMovementFlagsForModel(model as T, viewHolder.adapterPosition)
        } else {
            0
        }
    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        current as ListViewHolder
        target as ListViewHolder

        // By default we don't allow dropping on a model that isn't a drag target
        return isTouchableModel(target.requireModel())
    }

    protected open fun isTouchableModel(model: ListModel<*>): Boolean =
        targetModelClass?.java?.isInstance(model) ?: false

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        viewHolder as ListViewHolder

        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        (recyclerView.adapter as ListAdapter).moveModel(fromPosition, toPosition)

        val model = viewHolder.requireModel()
        check(isTouchableModel(model)) {
            "A model was dragged that is not a valid target: ${model.javaClass}"
        }

        onModelMoved(fromPosition, toPosition, model as T, viewHolder.itemView)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        viewHolder as ListViewHolder

        val model = viewHolder.requireModel()
        val view = viewHolder.itemView
        val position = viewHolder.adapterPosition

        check(isTouchableModel(model)) {
            "A model was swiped that is not a valid target: ${model.javaClass}"
        }

        onSwipeCompleted(model as T, view, position, direction)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (viewHolder != null) {
            viewHolder as ListViewHolder

            val model = viewHolder.requireModel()

            check(isTouchableModel(model)) {
                "A model was selected that is not a valid target: ${model.javaClass}"
            }

            (viewHolder.itemView.parent as RecyclerView).hasSelection = true

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                holderBeingSwiped = viewHolder
                onSwipeStarted(model as T, viewHolder.itemView, viewHolder.adapterPosition)
            } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                holderBeingDragged = viewHolder
                onDragStarted(model as T, viewHolder.itemView, viewHolder.adapterPosition)
            }
        } else if (holderBeingDragged != null) {
            onDragReleased(holderBeingDragged!!.model as T, holderBeingDragged!!.itemView)
            holderBeingDragged = null
        } else if (holderBeingSwiped != null) {
            onSwipeReleased(holderBeingSwiped!!.model as T, holderBeingSwiped!!.itemView)
            holderBeingSwiped = null
        }
    }

    private var RecyclerView.hasSelection: Boolean
        get() = getTag(R.id.epoxy_touch_helper_selection_status) == true
        set(value) {
            setTag(R.id.epoxy_touch_helper_selection_status, value)
        }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        viewHolder as ListViewHolder

        clearModelView(viewHolder.model as T, viewHolder.itemView)

        // If multiple touch helpers are in use, one touch helper can pick up buffered touch inputs
        // immediately after another touch event finishes. This leads to things like a view being
        // selected for drag when another view finishes its swipe off animation. To prevent that we
        // keep the recyclerview marked as having an active selection for a brief period after a
        // touch event ends.
        recyclerView.postDelayed({ recyclerView.hasSelection = false }, TOUCH_DEBOUNCE_MILLIS)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        viewHolder as ListViewHolder

        val model = viewHolder.requireModel()
        check(isTouchableModel(model)) {
            "A model was selected that is not a valid target: ${model.javaClass}"
        }

        val itemView = viewHolder.itemView

        val swipeProgress = if (Math.abs(dX) > Math.abs(dY)) {
            dX / itemView.width
        } else {
            dY / itemView.height
        }

        // Clamp to 1/-1 in the case of side padding where the view can be swiped extra
        val clampedProgress = Math.max(-1f, Math.min(1f, swipeProgress))

        onSwipeProgressChanged(model as T, itemView, clampedProgress, c)
    }

    override fun clearModelView(model: T, itemView: View) {
    }

    override fun onDragStarted(model: T, itemView: View, adapterPosition: Int) {
    }

    override fun onModelMoved(
        fromPosition: Int,
        toPosition: Int,
        modelBeingMoved: T,
        itemView: View
    ) {
    }

    override fun onDragReleased(model: T, itemView: View) {
    }

    override fun onSwipeStarted(model: T, itemView: View, adapterPosition: Int) {
    }

    override fun onSwipeProgressChanged(
        model: T,
        itemView: View,
        swipeProgress: Float,
        canvas: Canvas
    ) {
    }

    override fun onSwipeReleased(model: T, itemView: View) {
    }

    override fun onSwipeCompleted(model: T, itemView: View, position: Int, direction: Int) {
    }

    companion object {
        private const val TOUCH_DEBOUNCE_MILLIS = 300L
    }
}
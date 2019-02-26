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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ivianuu.essentials.ui.list.ListViewHolder

abstract class ListTouchHelperCallback : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        return getMovementFlags(recyclerView, viewHolder as ListViewHolder)
    }

    protected abstract fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: ListViewHolder
    ): Int

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        target: ViewHolder
    ): Boolean {
        return onMove(recyclerView, viewHolder as ListViewHolder, target as ListViewHolder)
    }

    protected abstract fun onMove(
        recyclerView: RecyclerView, viewHolder: ListViewHolder,
        target: ListViewHolder
    ): Boolean

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        onSwiped(viewHolder as ListViewHolder, direction)
    }

    protected abstract fun onSwiped(viewHolder: ListViewHolder, direction: Int)

    override fun canDropOver(
        recyclerView: RecyclerView, current: ViewHolder,
        target: ViewHolder
    ): Boolean {
        return canDropOver(recyclerView, current as ListViewHolder, target as ListViewHolder)
    }

    protected open fun canDropOver(
        recyclerView: RecyclerView,
        current: ListViewHolder,
        target: ListViewHolder
    ): Boolean {
        return super.canDropOver(recyclerView, current, target)
    }

    override fun getSwipeThreshold(viewHolder: ViewHolder): Float {
        return getSwipeThreshold(viewHolder as ListViewHolder)
    }

    protected open fun getSwipeThreshold(viewHolder: ListViewHolder): Float {
        return super.getSwipeThreshold(viewHolder)
    }

    override fun getMoveThreshold(viewHolder: ViewHolder): Float {
        return getMoveThreshold(viewHolder as ListViewHolder)
    }

    protected open fun getMoveThreshold(viewHolder: ListViewHolder): Float {
        return super.getMoveThreshold(viewHolder)
    }

    override fun chooseDropTarget(
        selected: ViewHolder,
        dropTargets: MutableList<ViewHolder>,
        curX: Int,
        curY: Int
    ): ViewHolder {
        return chooseDropTarget(
            selected as ListViewHolder, dropTargets as List<ListViewHolder>, curX,
            curY
        )
    }

    protected open fun chooseDropTarget(
        selected: ListViewHolder,
        dropTargets: List<ListViewHolder>, curX: Int, curY: Int
    ): ListViewHolder {


        return super.chooseDropTarget(
            selected,
            dropTargets,
            curX,
            curY
        ) as ListViewHolder
    }

    override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
        onSelectedChanged(viewHolder as ListViewHolder?, actionState)
    }

    protected open fun onSelectedChanged(viewHolder: ListViewHolder, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun onMoved(
        recyclerView: RecyclerView, viewHolder: ViewHolder, fromPos: Int,
        target: ViewHolder, toPos: Int, x: Int, y: Int
    ) {

        onMoved(
            recyclerView,
            viewHolder as ListViewHolder,
            fromPos,
            target as ListViewHolder,
            toPos,
            x,
            y
        )
    }

    protected open fun onMoved(
        recyclerView: RecyclerView, viewHolder: ListViewHolder, fromPos: Int,
        target: ListViewHolder, toPos: Int, x: Int, y: Int
    ) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
        clearView(recyclerView, viewHolder as ListViewHolder)
    }

    protected open fun clearView(recyclerView: RecyclerView, viewHolder: ListViewHolder) {
        super.clearView(recyclerView, viewHolder)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder,
        dX: Float,
        dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {

        onChildDraw(
            c, recyclerView, viewHolder as ListViewHolder, dX, dY, actionState,
            isCurrentlyActive
        )
    }

    protected open fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: ListViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onChildDrawOver(
        c: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder,
        dX: Float,
        dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {

        onChildDrawOver(
            c, recyclerView, viewHolder as ListViewHolder, dX, dY, actionState,
            isCurrentlyActive
        )
    }

    protected open fun onChildDrawOver(
        c: Canvas, recyclerView: RecyclerView, viewHolder: ListViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {

        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
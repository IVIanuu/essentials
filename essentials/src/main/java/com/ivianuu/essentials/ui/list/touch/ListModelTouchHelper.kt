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
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags
import androidx.recyclerview.widget.RecyclerView
import com.ivianuu.essentials.ui.list.ListModel
import java.util.*
import kotlin.reflect.KClass

object ListTouchHelper {

    fun initDragging(recyclerView: RecyclerView): DragBuilder {
        return DragBuilder(recyclerView)
    }

    class DragBuilder internal constructor(private val recyclerView: RecyclerView) {
        fun forVerticalList(): DragBuilder2 {
            return withDirections(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
        }

        fun forHorizontalList(): DragBuilder2 {
            return withDirections(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        }

        fun forGrid(): DragBuilder2 {
            return withDirections(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT
                        or ItemTouchHelper.RIGHT
            )
        }

        fun withDirections(directionFlags: Int): DragBuilder2 {
            return DragBuilder2(recyclerView, makeMovementFlags(directionFlags, 0))
        }
    }

    class DragBuilder2 internal constructor(
        private val recyclerView: RecyclerView,
        private val movementFlags: Int
    ) {

        fun <U : ListModel<*>> withTarget(targetModelClass: KClass<U>): DragBuilder3<U> {
            val targetClasses = ArrayList<KClass<out ListModel<*>>>(1)
            targetClasses.add(targetModelClass)

            return DragBuilder3(
                recyclerView, movementFlags, targetModelClass,
                targetClasses
            )
        }

        fun withTargets(vararg targetModelClasses: KClass<out ListModel<*>>): DragBuilder3<ListModel<*>> {
            return DragBuilder3(
                recyclerView, movementFlags, ListModel::class,
                targetModelClasses.toList()
            )
        }

        fun forAllModels(): DragBuilder3<ListModel<*>> {
            return withTarget(ListModel::class)
        }
    }

    class DragBuilder3<U : ListModel<*>> internal constructor(
        private val recyclerView: RecyclerView,
        private val movementFlags: Int,
        private val targetModelClass: KClass<U>,
        private val targetModelClasses: List<KClass<out ListModel<*>>>
    ) {

        fun andCallbacks(callbacks: DragCallbacks<U>): ItemTouchHelper {
            val itemTouchHelper =
                ItemTouchHelper(object : ListModelTouchCallback<U>(targetModelClass) {

                    override fun getMovementFlagsForModel(model: U, adapterPosition: Int): Int {
                        return movementFlags
                    }

                    override fun isTouchableModel(model: ListModel<*>): Boolean {
                        val isTargetType = if (targetModelClasses.size == 1) {
                            super.isTouchableModel(model)
                        } else {
                            targetModelClasses.contains(model::class)
                        }

                        return isTargetType && callbacks.isDragEnabledForModel(model as U)
                    }

                    override fun onDragStarted(model: U, itemView: View, adapterPosition: Int) {
                        callbacks.onDragStarted(model, itemView, adapterPosition)
                    }

                    override fun onDragReleased(model: U, itemView: View) {
                        callbacks.onDragReleased(model, itemView)
                    }

                    override fun onModelMoved(
                        fromPosition: Int,
                        toPosition: Int,
                        modelBeingMoved: U,
                        itemView: View
                    ) {
                        callbacks.onModelMoved(fromPosition, toPosition, modelBeingMoved, itemView)
                    }

                    override fun clearModelView(model: U, itemView: View) {
                        callbacks.clearModelView(model, itemView)
                    }

                })

            itemTouchHelper.attachToRecyclerView(recyclerView)

            return itemTouchHelper
        }
    }

    abstract class DragCallbacks<T : ListModel<*>> : ListDragCallback<T> {
        fun isDragEnabledForModel(model: T): Boolean = true
        override fun getMovementFlagsForModel(model: T, adapterPosition: Int): Int = 0

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

        override fun clearModelView(model: T, itemView: View) {
        }
    }

    fun initSwiping(recyclerView: RecyclerView): SwipeBuilder {
        return SwipeBuilder(recyclerView)
    }

    class SwipeBuilder internal constructor(private val recyclerView: RecyclerView) {

        fun right(): SwipeBuilder2 {
            return withDirections(ItemTouchHelper.RIGHT)
        }

        fun left(): SwipeBuilder2 {
            return withDirections(ItemTouchHelper.LEFT)
        }

        fun leftAndRight(): SwipeBuilder2 {
            return withDirections(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        }

        fun withDirections(directionFlags: Int): SwipeBuilder2 {
            return SwipeBuilder2(recyclerView, makeMovementFlags(0, directionFlags))
        }
    }

    class SwipeBuilder2 internal constructor(
        private val recyclerView: RecyclerView,
        private val movementFlags: Int
    ) {

        fun <U : ListModel<*>> withTarget(targetModelClass: KClass<U>): SwipeBuilder3<U> {
            val targetClasses = ArrayList<KClass<out ListModel<*>>>(1)
            targetClasses.add(targetModelClass)

            return SwipeBuilder3(
                recyclerView, movementFlags, targetModelClass,
                targetClasses
            )
        }

        fun withTargets(
            vararg targetModelClasses: KClass<out ListModel<*>>
        ): SwipeBuilder3<ListModel<*>> {
            return SwipeBuilder3(
                recyclerView, movementFlags, ListModel::class,
                targetModelClasses.toList()
            )
        }

        fun forAllModels(): SwipeBuilder3<ListModel<*>> {
            return withTarget(ListModel::class)
        }
    }

    class SwipeBuilder3<U : ListModel<*>> internal constructor(
        private val recyclerView: RecyclerView,
        private val movementFlags: Int,
        private val targetModelClass: KClass<U>,
        private val targetModelClasses: List<KClass<out ListModel<*>>>
    ) {

        fun andCallbacks(callbacks: SwipeCallbacks<U>): ItemTouchHelper {
            val itemTouchHelper =
                ItemTouchHelper(object : ListModelTouchCallback<U>(targetModelClass) {

                    override fun getMovementFlagsForModel(model: U, adapterPosition: Int): Int {
                        return movementFlags
                    }

                    override fun isTouchableModel(model: ListModel<*>): Boolean {
                        val isTargetType = if (targetModelClasses.size == 1)
                            super.isTouchableModel(model)
                        else
                            targetModelClasses.contains(model::class)


                        return isTargetType && callbacks.isSwipeEnabledForModel(model as U)
                    }

                    override fun onSwipeStarted(model: U, itemView: View, adapterPosition: Int) {
                        callbacks.onSwipeStarted(model, itemView, adapterPosition)
                    }

                    override fun onSwipeProgressChanged(
                        model: U, itemView: View, swipeProgress: Float,
                        canvas: Canvas
                    ) {
                        callbacks.onSwipeProgressChanged(model, itemView, swipeProgress, canvas)
                    }

                    override fun onSwipeCompleted(
                        model: U,
                        itemView: View,
                        position: Int,
                        direction: Int
                    ) {
                        callbacks.onSwipeCompleted(model, itemView, position, direction)
                    }

                    override fun onSwipeReleased(model: U, itemView: View) {
                        callbacks.onSwipeReleased(model, itemView)
                    }

                    override fun clearModelView(model: U, itemView: View) {
                        callbacks.clearModelView(model, itemView)
                    }

                })

            itemTouchHelper.attachToRecyclerView(recyclerView)

            return itemTouchHelper
        }
    }

    abstract class SwipeCallbacks<T : ListModel<*>> : ListSwipeCallback<T> {
        fun isSwipeEnabledForModel(model: T): Boolean = true
        override fun getMovementFlagsForModel(model: T, adapterPosition: Int): Int = 0

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

        override fun clearModelView(model: T, itemView: View) {
        }
    }
}
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

package com.ivianuu.essentials.ui.list.common

import android.graphics.Canvas
import android.view.View
import com.ivianuu.essentials.ui.list.ListModel

interface BaseListTouchCallback<T : ListModel<*>> {

    fun getMovementFlagsForModel(model: T, adapterPosition: Int): Int

    fun clearModelView(model: T, itemView: View)

}

interface ListDragCallback<T : ListModel<*>> : BaseListTouchCallback<T> {

    fun onDragStarted(model: T, itemView: View, adapterPosition: Int)

    fun onModelMoved(fromPosition: Int, toPosition: Int, modelBeingMoved: T, itemView: View)

    fun onDragReleased(model: T, itemView: View)

}

interface ListSwipeCallback<T : ListModel<*>> : BaseListTouchCallback<T> {

    fun onSwipeStarted(model: T, itemView: View, adapterPosition: Int)

    fun onSwipeProgressChanged(
        model: T,
        itemView: View,
        swipeProgress: Float,
        canvas: Canvas
    )

    fun onSwipeReleased(model: T, itemView: View)

    fun onSwipeCompleted(model: T, itemView: View, position: Int, direction: Int)
}


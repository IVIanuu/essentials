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

package com.ivianuu.essentials.util.ext

import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View

fun RecyclerView.doOnScrollStateChanged(action: ((recyclerView: RecyclerView, newState: Int) -> Unit)?) =
    addOnScrollListener(onScrollStateChanged = action)

fun RecyclerView.doOnScrolled(action: ((recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit)?) =
    addOnScrollListener(onScrolled = action)

fun RecyclerView.addOnScrollListener(
    onScrollStateChanged: ((recyclerView: RecyclerView, newState: Int) -> Unit)? = null,
    onScrolled: ((recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit)? = null
): RecyclerView.OnScrollListener {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            onScrollStateChanged?.invoke(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            onScrolled?.invoke(recyclerView, dx, dy)
        }
    }
    addOnScrollListener(listener)
    return listener
}

fun RecyclerView.doOnItemInterceptTouchEvent(action: (rv: RecyclerView, e: MotionEvent) -> Boolean) =
    addOnItemTouchListener(onInterceptTouchEvent = action)

fun RecyclerView.doOnItemTouchEvent(action: (rv: RecyclerView, e: MotionEvent) -> Unit) =
    addOnItemTouchListener(onTouchEvent = action)

fun RecyclerView.doOnItemRequestDisallowInterceptTouchEvent(action: (disallowIntercept: Boolean) -> Unit) =
    addOnItemTouchListener(onRequestDisallowInterceptTouchEvent = action)

fun RecyclerView.addOnItemTouchListener(
    onInterceptTouchEvent: ((rv: RecyclerView, e: MotionEvent) -> Boolean)? = null,
    onTouchEvent: ((rv: RecyclerView, e: MotionEvent) -> Unit)? = null,
    onRequestDisallowInterceptTouchEvent: ((disallowIntercept: Boolean) -> Unit)? = null
): RecyclerView.OnItemTouchListener {
    val listener = object : RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean =
            onInterceptTouchEvent?.invoke(rv, e) ?: false

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            onTouchEvent?.invoke(rv, e)
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            onRequestDisallowInterceptTouchEvent?.invoke(disallowIntercept)
        }
    }
    addOnItemTouchListener(listener)
    return listener
}

fun RecyclerView.doOnChildViewAttachedToWindow(action: (view: View) -> Unit) =
    addOnChildAttachStateChangeListener(onChildViewAttachedToWindow = action)

fun RecyclerView.doOnChildViewDetachedFromWindow(action: (view: View) -> Unit) =
    addOnChildAttachStateChangeListener(onChildViewDetachedFromWindow = action)

fun RecyclerView.addOnChildAttachStateChangeListener(
    onChildViewAttachedToWindow: ((view: View) -> Unit)? = null,
    onChildViewDetachedFromWindow: ((view: View) -> Unit)? = null
): RecyclerView.OnChildAttachStateChangeListener {
    val listener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            onChildViewAttachedToWindow?.invoke(view)
        }

        override fun onChildViewDetachedFromWindow(view: View) {
            onChildViewDetachedFromWindow?.invoke(view)
        }
    }
    addOnChildAttachStateChangeListener(listener)
    return listener
}

fun RecyclerView.Adapter<*>.doOnChanged(action: () -> Unit) =
    registerAdapterDataObserver(onChanged = action)

fun RecyclerView.Adapter<*>.doOnItemRangeChanged(action: (positionStart: Int, itemCount: Int, payload: Any?) -> Unit) =
    registerAdapterDataObserver(onItemRangeChanged = action)

fun RecyclerView.Adapter<*>.doOnItemRangeInserted(action: (positionStart: Int, itemCount: Int) -> Unit) =
    registerAdapterDataObserver(onItemRangeInserted = action)

fun RecyclerView.Adapter<*>.doOnItemRangeRemoved(action: (positionStart: Int, itemCount: Int) -> Unit) =
    registerAdapterDataObserver(onItemRangeRemoved = action)

fun RecyclerView.Adapter<*>.doOnItemRangeMoved(action: (fromPosition: Int, toPosition: Int, itemCount: Int) -> Unit) =
    registerAdapterDataObserver(onItemRangeMoved = action)

fun RecyclerView.Adapter<*>.registerAdapterDataObserver(
    onChanged: (() -> Unit?)? = null,
    onItemRangeChanged: ((positionStart: Int, itemCount: Int, payload: Any?) -> Unit)? = null,
    onItemRangeInserted: ((positionStart: Int, itemCount: Int) -> Unit)? = null,
    onItemRangeRemoved: ((positionStart: Int, itemCount: Int) -> Unit)? = null,
    onItemRangeMoved: ((fromPosition: Int, toPosition: Int, itemCount: Int) -> Unit)? = null
): RecyclerView.AdapterDataObserver {
    val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            onChanged?.invoke()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            onItemRangeChanged?.invoke(positionStart, itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onItemRangeInserted?.invoke(positionStart, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            onItemRangeRemoved?.invoke(positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            onItemRangeMoved?.invoke(fromPosition, toPosition, itemCount)
        }
    }
    registerAdapterDataObserver(observer)
    return observer
}
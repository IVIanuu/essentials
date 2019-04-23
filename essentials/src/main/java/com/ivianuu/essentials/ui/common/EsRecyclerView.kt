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

package com.ivianuu.essentials.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class EsRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var removedAdapter: Adapter<*>? = null

    var removeAdapterWhenDetachedFromWindow = true

    var delayMsWhenRemovingAdapterOnDetach = DEFAULT_ADAPTER_REMOVAL_DELAY_MS

    private var isRemoveAdapterRunnablePosted: Boolean = false
    private val removeAdapterRunnable = Runnable {
        if (isRemoveAdapterRunnablePosted) {
            isRemoveAdapterRunnablePosted = false
            removeAdapter()
        }
    }

    init {
        clipToPadding = false
        setRecycledViewPool(ACTIVITY_RECYCLER_POOL.getPool(context).viewPool)
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams) {
        val isFirstParams = layoutParams == null
        super.setLayoutParams(params)

        if (isFirstParams) {
            if (layoutManager == null) {
                layoutManager = createLayoutManager()
            }
        }
    }

    private fun createLayoutManager(): LayoutManager {
        val layoutParams = layoutParams

        return if (layoutParams.height == RecyclerView.LayoutParams.MATCH_PARENT
            || layoutParams.height == 0
        ) {
            if (layoutParams.width == RecyclerView.LayoutParams.MATCH_PARENT || layoutParams.width == 0) {
                setHasFixedSize(true)
            }

            LinearLayoutManager(context)
        } else {
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    fun clear() {
        swapAdapter(null, true)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        clearRemovedAdapterAndCancelRunnable()
    }

    override fun swapAdapter(
        adapter: Adapter<*>?,
        removeAndRecycleExistingViews: Boolean
    ) {
        super.swapAdapter(adapter, removeAndRecycleExistingViews)

        clearRemovedAdapterAndCancelRunnable()
    }

    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (removedAdapter != null) {
            swapAdapter(removedAdapter, false)
        }
        clearRemovedAdapterAndCancelRunnable()
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (removeAdapterWhenDetachedFromWindow) {
            if (delayMsWhenRemovingAdapterOnDetach > 0) {
                isRemoveAdapterRunnablePosted = true
                postDelayed(removeAdapterRunnable, delayMsWhenRemovingAdapterOnDetach.toLong())
            } else {
                removeAdapter()
            }
        }
        clearPoolIfActivityIsDestroyed()
    }

    private fun removeAdapter() {
        val currentAdapter = adapter
        if (currentAdapter != null) {
            swapAdapter(null, true)
            removedAdapter = currentAdapter
        }

        clearPoolIfActivityIsDestroyed()
    }

    private fun clearRemovedAdapterAndCancelRunnable() {
        removedAdapter = null
        if (isRemoveAdapterRunnablePosted) {
            removeCallbacks(removeAdapterRunnable)
            isRemoveAdapterRunnablePosted = false
        }
    }

    private fun clearPoolIfActivityIsDestroyed() {
        if (context.isActivityDestroyed()) {
            recycledViewPool.clear()
        }
    }

    companion object {
        private const val DEFAULT_ADAPTER_REMOVAL_DELAY_MS = 2000
        private val ACTIVITY_RECYCLER_POOL = ActivityRecyclerPool()
    }
}
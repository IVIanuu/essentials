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

package com.ivianuu.essentials.ui.simple

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ivianuu.essentials.R
import com.ivianuu.list.ItemController
import kotlinx.android.synthetic.main.es_view_recycler_view.es_recycler_view

/**
 * Simple controller
 */
abstract class ListController : ToolbarController() {

    override val layoutRes: Int
        get() = R.layout.es_controller_list

    val recyclerView: RecyclerView
        get() = es_recycler_view

    protected val itemController by lazy { itemController() }

    override fun onViewCreated(view: View, savedViewState: Bundle?) {
        super.onViewCreated(view, savedViewState)
        with(recyclerView) {
            adapter = itemController.adapter
            layoutManager()?.let { layoutManager = it }
        }
    }

    override fun onDestroyView(view: View) {
        itemController.cancelPendingItemBuild()
        super.onDestroyView(view)
    }

    override fun invalidate() {
        itemController.requestItemBuild()
    }

    protected abstract fun itemController(): ItemController

    protected open fun layoutManager(): RecyclerView.LayoutManager? = null

}
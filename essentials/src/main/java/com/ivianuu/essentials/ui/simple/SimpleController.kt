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
import com.ivianuu.essentials.ui.common.EsRecyclerView
import com.ivianuu.list.ItemController

/**
 * Simple controller
 */
abstract class SimpleController : ToolbarController() {

    override val layoutRes: Int
        get() = R.layout.es_controller_simple

    val optionalItemController: ItemController? by lazy { itemController() }
    val itemController
        get() = optionalItemController
            ?: error("no model controller instantiated")

    val recyclerView
        get() = optionalRecyclerView ?: error("no recycler view found")

    open val optionalRecyclerView: EsRecyclerView?
        get() = containerView?.findViewById(R.id.es_recycler_view)

    override fun onViewCreated(view: View, savedViewState: Bundle?) {
        super.onViewCreated(view, savedViewState)
        optionalRecyclerView?.run {
            adapter = optionalItemController?.adapter
            this@SimpleController.layoutManager()?.let { layoutManager = it }
        }
    }

    override fun onDestroyView(view: View) {
        optionalItemController?.cancelPendingItemBuild()
        super.onDestroyView(view)
    }

    override fun invalidate() {
        optionalItemController?.requestItemBuild()
    }

    protected open fun itemController(): ItemController? = null

    protected open fun layoutManager(): RecyclerView.LayoutManager? = null

}
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

package com.ivianuu.essentials.ui.listessentials

import android.content.Context
import android.view.View
import com.ivianuu.essentials.ui.list.ListModel
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.ScopeOwner

/**
 * Base list model with holder
 */
abstract class EsListModel<H : EsListHolder> : ListModel<H>(), ContextAware, ScopeOwner {

    override lateinit var providedContext: Context

    var onClick: ((View) -> Unit)? by optionalProperty("onClick")
    var onLongClick: ((View) -> Boolean)? by optionalProperty("onLongClick")

    protected open val useContainerForClicks get() = true
    protected open val useContainerForLongClicks get() = true

    override val scope: Scope
        get() {
            if (_scope == null) _scope = ReusableScope()
            return _scope!!
        }

    private var _scope: ReusableScope? = null

    override fun onBind(holder: H) {
        _scope?.clear()
        super.onBind(holder)

        val onClickView = getOnClickView(holder)
        if (onClickView != null) {
            onClickView.setOnClickListener(onClick)
        } else if (useContainerForClicks) {
            holder.containerView.setOnClickListener(onClick)
        }

        val onLongClickView = getOnLongClickView(holder)
        if (onLongClickView != null) {
            onLongClickView.setOnLongClickListener(onLongClick)
        } else if (useContainerForLongClicks) {
            holder.containerView.setOnLongClickListener(onLongClick)
        }
    }

    override fun onUnbind(holder: H) {
        _scope?.clear()

        val onClickView = getOnClickView(holder)
        if (onClickView != null) {
            onClickView.setOnClickListener(null)
        } else if (useContainerForClicks) {
            holder.containerView.setOnClickListener(null)
        }

        val onLongClickView = getOnLongClickView(holder)
        if (onLongClickView != null) {
            onLongClickView.setOnClickListener(null)
        } else if (useContainerForLongClicks) {
            holder.containerView.setOnLongClickListener(null)
        }

        super.onUnbind(holder)
    }

    protected open fun getOnClickView(holder: H): View? = null
    protected open fun getOnLongClickView(holder: H): View? = null

}
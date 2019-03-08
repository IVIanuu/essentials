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

package com.ivianuu.essentials.ui.epoxy

import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.ScopeOwner

/**
 * Base epoxy model with holder
 */
abstract class EsEpoxyModel<H : EsEpoxyHolder> : EpoxyModelWithHolder<H>(), ContextAware,
    ScopeOwner {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var onClick: ((View) -> Unit)? = null
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var onLongClick: ((View) -> Boolean)? = null

    override lateinit var providedContext: Context

    protected open val onClickView: View? get() = null
    protected open val useContainerForClicks: Boolean get() = true

    protected open val onLongClickView: View? get() = null
    protected open val useContainerForLongClicks: Boolean get() = true

    override val scope: Scope
        get() {
            if (_scope == null) _scope = ReusableScope()
            return _scope!!
        }

    private var _scope: ReusableScope? = null

    @CallSuper
    override fun bind(holder: H) {
        _scope?.clear()
        providedContext = holder.containerView.context

        super.bind(holder)

        if (onClickView != null) {
            onClickView?.setOnClickListener(onClick)
        } else if (useContainerForClicks) {
            holder.containerView.setOnClickListener(onClick)
        }

        if (onLongClickView != null) {
            onLongClickView?.setOnLongClickListener(onLongClick)
        } else if (useContainerForLongClicks) {
            holder.containerView.setOnLongClickListener(onLongClick)
        }
    }

    @CallSuper
    override fun unbind(holder: H) {
        if (onClickView != null) {
            onClickView?.setOnClickListener(null)
        } else if (useContainerForClicks) {
            holder.containerView.setOnClickListener(null)
        }

        if (onLongClickView != null) {
            onLongClickView?.setOnClickListener(null)
        } else if (useContainerForLongClicks) {
            holder.containerView.setOnLongClickListener(null)
        }

        _scope?.clear()

        super.unbind(holder)
    }

}
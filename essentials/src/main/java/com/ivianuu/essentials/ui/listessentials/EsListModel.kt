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

import android.view.View
import com.ivianuu.essentials.ui.list.ListModel
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.ScopeOwner
import kotlinx.android.extensions.LayoutContainer

/**
 * Base list model with holder
 */
abstract class EsListModel<H : EsListHolder> : ListModel<H>(), ContextAware, LayoutContainer,
    ScopeOwner {

    override val containerView
        get() = _boundHolder!!.containerView

    override val providedContext
        get() = _boundHolder!!.providedContext

    protected open val onClickView: View? get() = null
    protected open val useContainerForClicks get() = true

    protected open val onLongClickView: View? get() = null
    protected open val useContainerForLongClicks get() = true

    private var _boundHolder: H? = null

    override val scope: Scope
        get() {
            if (_scope == null) _scope = ReusableScope()
            return _scope!!
        }

    private var _scope: ReusableScope? = null

    override fun onBind(holder: H) {
        _scope?.clear()
        _boundHolder = holder
        super.onBind(holder)
    }

    override fun onUnbind(holder: H) {
        _scope?.clear()
        _boundHolder = null
        super.onUnbind(holder)
    }

}
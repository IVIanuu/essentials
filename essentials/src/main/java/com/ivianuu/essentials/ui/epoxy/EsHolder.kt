/*
 * Copyright 2019 Manuel Wrage
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
import com.airbnb.epoxy.EpoxyHolder
import com.ivianuu.essentials.util.ContextAware
import kotlinx.android.extensions.LayoutContainer

/**
 * Base list holder
 */
open class EsHolder : EpoxyHolder(), ContextAware, LayoutContainer {

    lateinit var root: View
        private set

    override val providedContext: Context
        get() = root.context

    override val containerView: View?
        get() = root

    private var _cachedViews: MutableMap<Int, View>? = null

    override fun bindView(itemView: View) {
        root = itemView
    }

    fun <T : View> findView(id: Int): T = if (_cachedViews == null) {
        _cachedViews = mutableMapOf()
        val view = root.findViewById<T>(id)
        _cachedViews!![id] = view
        view
    } else {
        _cachedViews!!.getOrPut(id) { root.findViewById(id) } as T
    }
}
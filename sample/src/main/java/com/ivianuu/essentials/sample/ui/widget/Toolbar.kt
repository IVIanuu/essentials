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

package com.ivianuu.essentials.sample.ui.widget

import android.view.ViewGroup
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.lib.ViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.kommon.core.view.inflate

open class Toolbar(
    val leading: Widget<*>? = null,
    val title: Widget<*>? = null,
    val trailing: List<Widget<*>>? = null,
    override val key: Any? = null
) : ViewGroupWidget<ViewGroup>() {

    override fun createView(container: ViewGroup): ViewGroup =
        container.inflate<ViewGroup>(R.layout.view_toolbar)

    override fun buildChildren() {
        super.buildChildren()
        if (leading != null) emit(leading, R.id.leading_container)
        if (title != null) emit(title, R.id.title_container)
        trailing?.forEach { emit(it, R.id.trailing_container) }
    }

}
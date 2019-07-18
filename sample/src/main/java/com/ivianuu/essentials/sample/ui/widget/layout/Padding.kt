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

package com.ivianuu.essentials.sample.ui.widget.layout

import android.view.ViewGroup
import android.widget.FrameLayout
import com.ivianuu.essentials.sample.ui.widget.lib.ViewGroupWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget

open class Padding(
    override val key: Any? = null,
    val child: Widget<*>,
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0
) : ViewGroupWidget<FrameLayout>() {

    constructor(
        key: Any? = null,
        child: Widget<*>,
        padding: Int
    ) : this(key, child, padding, padding, padding, padding)

    override fun createView(container: ViewGroup) = FrameLayout(container.context).apply {
        setPadding(
            this@Padding.left,
            this@Padding.top,
            this@Padding.right,
            this@Padding.bottom
        )
    }

    override fun children() {
        emit(child)
    }

}
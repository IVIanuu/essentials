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
import android.widget.LinearLayout
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ViewGroupWidget

open class Flex(
    val orientation: Int,
    override val key: Any? = null,
    val gravity: Int,
    val buildChildren: BuildContext.() -> Unit
) : ViewGroupWidget<LinearLayout>() {

    init {
        state(orientation, gravity)
    }

    override fun createView(container: ViewGroup) = LinearLayout(container.context).apply {
        orientation = this@Flex.orientation
        gravity = this@Flex.gravity
    }

    override fun buildChildren() {
        buildChildren.invoke(this)
    }

}
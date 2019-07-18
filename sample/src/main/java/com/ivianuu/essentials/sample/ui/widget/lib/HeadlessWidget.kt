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

package com.ivianuu.essentials.sample.ui.widget.lib

import android.view.View
import android.view.ViewGroup
import com.ivianuu.essentials.util.cast

abstract class HeadlessWidget(
    private val child: Widget<*>
) : Widget<View>() {

    override val key: Any?
        get() = child.key

    override val viewId: Int
        get() = child.viewId

    override fun layout(view: View) {
        super.layout(view)
        child.cast<Widget<View>>().layout(view)
    }

    override fun bind(view: View) {
        super.bind(view)
        child.cast<Widget<View>>().bind(view)
    }

    override fun unbind(view: View) {
        super.unbind(view)
        child.cast<Widget<View>>().unbind(view)
    }

    override fun createView(container: ViewGroup): View = child.createView(container)

    override fun BuildContext.children() {
        emit(child, containerId)
    }

}
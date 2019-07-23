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
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ContainerAmbient
import com.ivianuu.essentials.sample.ui.widget.lib.ViewGroupWidget

class IdViewGroupWidget<V : ViewGroup>(
    val id: Int,
    key: Any? = null,
    children: BuildContext.() -> Unit
) : ViewGroupWidget<V>(key, children) {
    override fun createView(context: BuildContext): V {
        val container = ContainerAmbient(context)
        return container.findViewById<V>(id).also {
            container.removeView(it) // todo remove this hack
        }
    }
}
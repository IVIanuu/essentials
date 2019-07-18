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

import com.github.ajalt.timberkt.d

interface BuildContext {
    val widgetContext: WidgetContext
    fun emit(component: Widget<*>, containerId: Int? = null) {
    }
}

class WidgetBuildContext(
    override val widgetContext: WidgetContext,
    private val thisWidget: Widget<*>
) : BuildContext {
    override fun emit(component: Widget<*>, containerId: Int?) {
        // todo check duplicate
        d { "emit ${thisWidget.javaClass.simpleName} -> ${component.javaClass.simpleName}" }
        if (thisWidget.children == null) thisWidget.children = mutableListOf()
        component.parent = thisWidget
        component.containerId = containerId
        component.buildChildren(WidgetBuildContext(widgetContext, component))
        thisWidget.children!!.add(component)
    }

}
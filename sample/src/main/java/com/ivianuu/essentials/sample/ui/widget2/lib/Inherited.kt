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

package com.ivianuu.essentials.sample.ui.widget2.lib

import com.ivianuu.essentials.util.cast
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.typeOf

abstract class InheritedWidget(
    child: Widget,
    val type: Type<out InheritedWidget>? = null,
    key: Any? = null
) : ProxyWidget(child, key) {
    override fun createElement() = InheritedElement(this)

    abstract fun updateShouldNotify(oldWidget: InheritedWidget): Boolean
}

open class InheritedElement(widget: InheritedWidget) : ProxyElement(widget) {

    protected val dependents = mutableSetOf<Element>()

    override fun updateInheritance() {
        val inheritedWidgets =
            parent?.inheritedWidgets ?: mutableMapOf()
        this.inheritedWidgets = inheritedWidgets

        val type = if (widget<InheritedWidget>().type != null) {
            widget<InheritedWidget>().type!!
        } else {
            typeOf(this::class)
        }

        inheritedWidgets[type] = this
    }

    open fun updateDependencies(dependent: Element) {
        dependents.add(dependent)
    }

    protected open fun notifyDependent(oldWidget: Widget, dependent: Element) {
        dependent.didChangeDependencies()
    }

    override fun updated(oldWidget: Widget) {
        if (widget<InheritedWidget>().updateShouldNotify(oldWidget.cast())) {
            super.updated(oldWidget)
        }
    }

    override fun notifyClients(oldWidget: Widget) {
        dependents.forEach { notifyDependent(oldWidget, it) }
    }
}
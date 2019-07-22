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

abstract class State {
    var element: StatefulElement? = null
    var widget: StatefulWidget? = null

    val context: BuildContext? get() = element

    abstract fun build(context: BuildContext): Widget

    open fun initState() {
    }

    open fun dispose() {
    }

    open fun didUpdateWidget(oldWidget: Widget) {
    }

    open fun didChangeDependencies() {
    }

    open fun setState(block: () -> Unit) {
        block()
        element?.markNeedsBuild()
    }

    inline fun <reified T : Element> element(): T = element as T

    inline fun <reified T : Widget> widget(): T = widget as T

}

abstract class StatefulWidget(key: Any? = null) : Widget(key) {

    override fun createElement(): StatefulElement = StatefulElement(this)

    abstract fun createState(): State

}

open class StatefulElement(widget: StatefulWidget) : ComponentElement(widget) {

    var state: State? = widget.createState()

    init {
        state!!.element = this
        state!!.widget = widget
    }

    override fun build(): Widget = state!!.build(this)

    override fun firstBuild() {
        super.firstBuild()
        state!!.initState()
    }

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        val oldWidget = state!!.widget!!
        isDirty = true
        state!!.widget = widget()
        state!!.didUpdateWidget(oldWidget)
        rebuild()
    }

    override fun didChangeDependencies() {
        super.didChangeDependencies()
        state!!.didChangeDependencies()
    }

    override fun unmount() {
        super.unmount()
        state!!.dispose()
        state!!.element = null
        state!!.widget = null
        state = null
    }
}
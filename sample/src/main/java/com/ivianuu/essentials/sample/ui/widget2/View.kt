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

package com.ivianuu.essentials.sample.ui.widget2

import android.content.Context
import android.view.View
import com.ivianuu.essentials.util.cast

abstract class ViewWidget<V : View> : Widget() {
    abstract override fun createElement(): ViewElement<V>

    abstract fun createView(context: BuildContext, androidContext: Context): V

    open fun updateView(context: BuildContext, view: V) {
    }
}

abstract class ViewElement<V : View>(widget: ViewWidget<V>) : Element(widget) {

    var view: V? = null
        private set

    private var ancestorViewElement: ViewElement<*>? = null

    abstract fun insertChild(view: View, slot: Int?)

    abstract fun moveChild(view: View, slot: Int?)

    abstract fun removeChild(view: View)

    override fun mount(context: Context, parent: Element?, slot: Int?) {
        super.mount(context, parent, slot)
        view = widget.cast<ViewWidget<V>>().createView(this, context)
        widget.cast<ViewWidget<V>>().updateView(this, view!!)
    }

    override fun attachView() {
        val ancestorViewElement = findAncestorViewElement() ?: error("")
        this.ancestorViewElement = ancestorViewElement
        ancestorViewElement.insertChild(view!!, slot)
    }

    override fun detachView() {
        ancestorViewElement!!.removeChild(view!!)
    }

    override fun update(context: Context, newWidget: Widget) {
        super.update(context, newWidget)
        widget.cast<ViewWidget<V>>().updateView(this, view!!)
    }

    override fun unmount() {
        super.unmount()
        view = null
    }

    private fun findAncestorViewElement(): ViewElement<*>? {
        var ancestor = parent
        while (ancestor != null) {
            if (ancestor is ViewElement<*>) return ancestor
            ancestor = ancestor.parent
        }

        return null
    }
}
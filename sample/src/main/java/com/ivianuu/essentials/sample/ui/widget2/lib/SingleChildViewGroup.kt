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

import android.content.Context
import android.view.View
import android.view.ViewGroup

abstract class SingleChildViewGroupWidget<V : ViewGroup>(
    val child: Widget,
    key: Any? = null
) : ViewGroupWidget<V>(listOf(child), key)

open class SingleChildViewGroup<V : ViewGroup>(widget: SingleChildViewGroupWidget<V>) :
    ViewElement<V>(widget) {

    var child: Element? = null
        protected set

    override fun mount(context: Context, parent: Element?, slot: Int?) {
        super.mount(context, parent, slot)
        child = widget<SingleChildViewGroupWidget<V>>().createElement()
        child!!.mount(context, parent, null)
    }

    override fun insertChildView(view: View, slot: Int?) {
        requireView().addView(view)
    }

    override fun moveChildView(view: View, slot: Int?) {
    }

    override fun removeChildView(view: View) {
        requireView().removeView(view)
    }

    override fun attachView() {
        super.attachView()
        child!!.attachView()
    }

    override fun detachView() {
        child!!.detachView()
        super.detachView()
    }

    override fun unmount() {
        child!!.unmount()
        child = null
        super.unmount()
    }

    override fun update(context: Context, newWidget: Widget) {
        super.update(context, newWidget)
        child = updateChild(context, child, newWidget, null)
    }

    override fun onEachChild(block: (Element) -> Unit) {
        child?.let(block)
    }
}
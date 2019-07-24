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

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.github.ajalt.timberkt.d
import kotlin.reflect.KClass

typealias CreateView<V> = (ViewGroup) -> V
typealias UpdateView<V> = (V) -> Unit

inline fun <reified V : View> ViewWidget(
    key: Any? = null,
    noinline updateView: UpdateView<V>? = null
) = ViewWidget(V::class, key, updateView)

fun <V : View> ViewWidget(
    type: KClass<V>,
    key: Any? = null,
    updateView: UpdateView<V>? = null
) = ViewWidget(
    viewType = type,
    key = key,
    createView = {
        type.java.getDeclaredConstructor(Context::class.java)
            .newInstance(it.context)
    },
    updateView = updateView
)

inline fun <reified V : View> ViewWidget(
    key: Any? = null,
    noinline createView: CreateView<V>,
    noinline updateView: UpdateView<V>? = null
) = ViewWidget(
    viewType = V::class,
    key = key,
    createView = createView,
    updateView = updateView
)


fun <V : View> ViewWidget(
    viewType: KClass<V>,
    key: Any? = null,
    createView: CreateView<V>,
    updateView: UpdateView<V>? = null
): Widget = object : ViewWidget<V>(joinKey(viewType, key)) {
    override fun createView(container: ViewGroup): V = createView.invoke(container)
    override fun updateView(view: V) {
        super.updateView(view)
        updateView?.invoke(view)
    }
}

abstract class ViewWidget<V : View>(key: Any? = null) : Widget(key) {
    override fun createElement(): ViewElement<V> = ViewElement(this)

    abstract fun createView(container: ViewGroup): V

    open fun updateView(view: V) {
    }
}

open class ViewElement<V : View>(widget: ViewWidget<V>) : Element(widget) {

    var view: V? = null
        private set

    private var ancestorViewElement: ViewElement<*>? = null

    open fun insertChildView(view: View, slot: Int?) {
        error("unsupported")
    }

    open fun moveChildView(view: View, slot: Int?) {
        error("unsupported")
    }

    open fun removeChildView(view: View) {
        error("unsupported")
    }

    override fun mount(
        parent: Element?,
        slot: Int?
    ) {
        super.mount(parent, slot)
        // todo manage ancestor better
        view = widget<ViewWidget<V>>().createView(findContainerView())
        updateView()
        attachView()
    }

    override fun attachView() {
        val ancestorViewElement = findAncestorViewElement()!!
        d { "${widget.key} attach to $ancestorViewElement view is $view" }
        this.ancestorViewElement = ancestorViewElement
        ancestorViewElement.insertChildView(requireView(), slot)
    }

    override fun detachView() {
        d { "${widget.key} remove from $ancestorViewElement view is $view" }
        if (ancestorViewElement != null) {
            ancestorViewElement!!.removeChildView(requireView())
            ancestorViewElement = null
        }
    }

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        updateView()
    }

    override fun updateSlot(newSlot: Int?) {
        super.updateSlot(newSlot)
        ancestorViewElement!!.moveChildView(requireView(), slot)
    }

    override fun unmount() {
        super.unmount()
        view = null
    }

    protected open fun findContainerView(): ViewGroup {
        return findAncestorViewElement()!!.view as ViewGroup
    }

    private fun findAncestorViewElement(): ViewElement<*>? {
        var ancestor = parent
        while (ancestor != null) {
            if (ancestor is ViewElement<*>) return ancestor
            ancestor = ancestor.parent
        }

        return null
    }

    private fun updateView() {
        updateLayoutParams()
        updateViewProps()
        widget<ViewWidget<V>>().updateView(requireView())
        isDirty = false
    }

    private fun collectLayoutParams(): List<LayoutParamsElement> {
        val params = mutableListOf<LayoutParamsElement>()
        var ancestor = parent
        while (ancestor != null && ancestor !is ViewElement<*>) {
            if (ancestor is LayoutParamsElement) params.add(ancestor)
            ancestor = ancestor.parent
        }

        return params.reversed()
    }

    private fun updateLayoutParams() {
        val view = requireView()
        val lp = view.layoutParams
            ?: genDefaultLayoutParams.invoke(findContainerView()) as ViewGroup.LayoutParams
        collectLayoutParams().forEach {
            d { "${widget.key} update lp $it" }
            it.updateLayoutParams(lp)
        }
        view.layoutParams = lp
    }


    private fun updateViewProps() {
        collectViewProps().forEach { it.updateViewProps(requireView()) }
    }

    private fun collectViewProps(): List<ViewPropsElement> {
        val props = mutableListOf<ViewPropsElement>()
        var ancestor = parent
        while (ancestor != null && ancestor !is ViewElement<*>) {
            if (ancestor is ViewPropsElement) props.add(ancestor)
            ancestor = ancestor.parent
        }

        return props.reversed()
    }

    protected fun requireView(): V = this.view ?: error("not mounted")

    override fun performRebuild() {
        d { "${widget.key} perform rebuild" }
        updateView()
    }

}

private val genDefaultLayoutParams by lazy {
    val method = ViewGroup::class.java.getDeclaredMethod("generateDefaultLayoutParams")
    method.isAccessible = true
    method
}
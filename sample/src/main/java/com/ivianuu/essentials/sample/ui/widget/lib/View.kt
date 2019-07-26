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

typealias UpdateLayoutParams = (ViewGroup.LayoutParams) -> Boolean
typealias CreateView<V> = (ViewGroup) -> V
typealias UpdateView<V> = (V) -> Unit

inline fun <reified V : View> BuildContext.ViewWidget(
    key: Any? = null,
    noinline updateView: UpdateView<V>? = null
) = ViewWidget(V::class, key, updateView)

fun <V : View> BuildContext.ViewWidget(
    type: KClass<V>,
    key: Any? = null,
    updateView: UpdateView<V>? = null
) = ViewWidget(
    key = key,
    createView = {
        type.java.getDeclaredConstructor(Context::class.java)
            .newInstance(it.context)
    },
    updateView = updateView
)

fun <V : View> ViewWidget(
    key: Any? = null,
    createView: CreateView<V>,
    updateView: UpdateView<V>? = null
): Widget = object : ViewWidget<V>(key) {
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

    override fun createView() {
        super.createView()
        // todo manage ancestor better
        view = widget<ViewWidget<V>>().createView(findContainerView())
    }

    override fun destroyView() {
        super.destroyView()
        view = null
    }

    override fun attachView() {
        super.attachView()
        updateView()
        val ancestorViewElement = findAncestorViewElement()!!
        d { "${widget.id} attach to $ancestorViewElement view is $view" }
        this.ancestorViewElement = ancestorViewElement
        ancestorViewElement.insertChildView(requireView(), slot)
    }

    override fun detachView() {
        super.detachView()
        d { "${widget.id} remove from $ancestorViewElement view is $view" }
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

    fun updateView() {
        d { "update view ${widget.id}" }
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
        var changed = false
        collectLayoutParams().forEach {
            if (it.updateLayoutParams(lp)) {
                changed = true
            }
        }

        d { "${widget.id} updated lp has changed ? ${changed || view.layoutParams == null}" }

        if (changed || view.layoutParams == null) {
            view.layoutParams = lp
        }
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

    protected fun requireView(): V = this.view ?: error("view not created")

    override fun performRebuild() {
        d { "${widget.id} perform rebuild" }
        updateView()
    }

}

private val genDefaultLayoutParams by lazy {
    val method = ViewGroup::class.java.getDeclaredMethod("generateDefaultLayoutParams")
    method.isAccessible = true
    method
}
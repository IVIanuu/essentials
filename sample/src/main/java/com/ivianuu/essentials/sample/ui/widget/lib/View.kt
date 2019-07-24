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

typealias CreateView<V> = BuildContext.() -> V
typealias UpdateView<V> = BuildContext.(V) -> Unit

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
            .newInstance((+AndroidContextAmbient))
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
    override fun createView(context: BuildContext): V = createView.invoke(context)
    override fun updateView(context: BuildContext, view: V) {
        super.updateView(context, view)
        updateView?.invoke(context, view)
    }
}

abstract class ViewWidget<V : View>(key: Any? = null) : Widget(key) {
    override fun createElement(): ViewElement<V> = ViewElement(this)

    abstract fun createView(context: BuildContext): V

    open fun updateView(context: BuildContext, view: V) {
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
        view = widget<ViewWidget<V>>().createView(this)
        attachView()
    }

    override fun attachView() {
        val ancestorViewElement = findAncestorViewElement()!!
        d { "${javaClass.simpleName} attach to $ancestorViewElement view is $view" }
        this.ancestorViewElement = ancestorViewElement
        applyViewProps()
        ancestorViewElement.insertChildView(requireView(), slot)
    }

    override fun detachView() {
        d { "${javaClass.simpleName} remove from $ancestorViewElement view is $view" }
        if (ancestorViewElement != null) {
            ancestorViewElement!!.removeChildView(requireView())
            ancestorViewElement = null
        }
    }

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        applyViewProps()
        widget<ViewWidget<V>>().updateView(this, requireView())
        isDirty = false
    }

    fun updateViewProps(viewProps: ViewPropsWidget) {
        viewProps.applyViewProps(requireView())
    }

    override fun updateSlot(newSlot: Int?) {
        super.updateSlot(newSlot)
        ancestorViewElement!!.moveChildView(requireView(), slot)
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

    private fun collectViewProps(): List<ViewPropsElement> {
        val props = mutableListOf<ViewPropsElement>()
        var ancestor = parent
        while (ancestor != null && ancestor !is ViewElement<*>) {
            if (ancestor is ViewPropsElement) props.add(ancestor)
            ancestor = ancestor.parent
        }

        return props.reversed()
    }

    private fun applyViewProps() {
        // todo optimize performance
        // todo add a LayoutParamsWidget and merge all mutations
        // ensure we got some lp
        if (requireView().layoutParams == null) {
            requireView().layoutParams =
                genDefaultLayoutParams.invoke(findAncestorViewElement()!!.view) as? ViewGroup.LayoutParams
        }

        collectViewProps().forEach { it.applyViewProps(requireView()) }
    }

    protected fun requireView(): V = this.view ?: error("not mounted")

    override fun performRebuild() {
        d { "${javaClass.simpleName} perform rebuild $widget" }
        widget<ViewWidget<V>>().updateView(this, requireView())
        isDirty = false
    }

}

// TODO(lmr): This should be moved to a separate module, but needs to be one that is not IR-compiled
private val genDefaultLayoutParams by lazy {
    val method = ViewGroup::class.java.getDeclaredMethod("generateDefaultLayoutParams")
    method.isAccessible = true
    method
}